package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.TierTransitionResult;
import com.firstclub.membership.dto.request.CancelMembershipRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.eligibility.TierEligibilityEngine;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.AlreadySubscribedException;
import com.firstclub.membership.exception.InvalidTierTransitionException;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.AuditService;
import com.firstclub.membership.service.MembershipLifecycleService;
import com.firstclub.membership.util.ChangeHistoryUtil;
import com.firstclub.membership.util.MembershipDateUtils;
import com.firstclub.membership.util.UserMembershipUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MembershipLifecycleServiceImpl implements MembershipLifecycleService {

    private final UserMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierEligibilityEngine eligibilityEngine;
    private final AuditService<MembershipChangeHistory> auditService;

    @Override
    @Transactional
    public UserMembership createSubscription(SubscribeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (membershipRepository.existsByUser_IdAndStatus(request.getUserId(), MembershipStatus.ACTIVE)) {
            throw new AlreadySubscribedException(request.getUserId());
        }

        MembershipPlan plan = planRepository.findByIdAndActiveTrue(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", request.getPlanId()));

        MembershipTier tier = resolveEligibleTierForUser(user);

        return membershipRepository.save(UserMembershipUtil.buildUserMembership(user, plan, tier, request));
    }

    @Override
    @Transactional
    public TierTransitionResult evaluateAndApplyUpgrade(Long membershipId) {
        UserMembership membership = fetchActiveMembership(membershipId);
        User user = membership.getUser();
        EligibilityContext context = UserMembershipUtil.buildEligibilityContextWithoutOrderDetails(user);

        return eligibilityEngine.resolveEligibleTier(context)
                .map(eligibleTier -> {
                    MembershipTier currentTier = membership.getMembershipTier();
                    if (eligibleTier.getTierLevel() > currentTier.getTierLevel()) {
                        membership.setMembershipTier(eligibleTier);
                        UserMembership upgraded = membershipRepository.save(membership);
                        return TierTransitionResult.builder()
                                .user(user)
                                .membership(upgraded)
                                .fromTier(currentTier)
                                .toTier(eligibleTier)
                                .tierChanged(true)
                                .build();
                    }
                    return UserMembershipUtil.buildNoChangeTierTransition(user, membership);
                })
                .orElse(UserMembershipUtil.buildNoChangeTierTransition(user, membership));
    }

    @Override
    @Transactional
    public TierTransitionResult applyDowngrade(Long membershipId) {
        UserMembership membership = fetchActiveMembership(membershipId);
        MembershipTier fromTier = membership.getMembershipTier();
        User user = membership.getUser();

        EligibilityContext context = UserMembershipUtil.buildEligibilityContextWithoutOrderDetails(user);
        MembershipTier eligibleTier = eligibilityEngine.resolveEligibleTier(context)
                .orElseGet(() -> tierRepository.findFirstByActiveTrueOrderByTierLevelAsc()
                        .orElseThrow(() -> new MembershipException("No active tiers found")));

        if (eligibleTier.getTierLevel() >= fromTier.getTierLevel()) {
            throw new InvalidTierTransitionException(
                    "User is still eligible for tier: " + eligibleTier.getName());
        }

        membership.setMembershipTier(eligibleTier);

        return TierTransitionResult.builder()
                .user(user)
                .membership(membershipRepository.save(membership))
                .fromTier(fromTier)
                .toTier(eligibleTier)
                .tierChanged(true)
                .build();
    }

    @Override
    @Transactional
    public UserMembership cancel(Long membershipId, CancelMembershipRequest request) {
        UserMembership membership = fetchActiveMembership(membershipId);
        membership.setStatus(MembershipStatus.CANCELLED);
        membership.setCancelledAt(LocalDateTime.now());
        membership.setCancellationReason(request.getReason());
        return membershipRepository.save(membership);
    }

    @Override
    @Transactional
    public UserMembership expireIfNeeded(UserMembership membership) {
        if (MembershipStatus.ACTIVE.equals(membership.getStatus())
                && MembershipDateUtils.isExpired(membership.getExpiryDate())) {
            membership.setStatus(MembershipStatus.EXPIRED);
            UserMembership expired = membershipRepository.save(membership);
            auditService.record(ChangeHistoryUtil.forExpiry(expired));
            return expired;
        }
        return membership;
    }

    private MembershipTier resolveEligibleTierForUser(User user) {
        EligibilityContext context = UserMembershipUtil.buildEligibilityContextWithoutOrderDetails(user);
        return eligibilityEngine.resolveEligibleTier(context)
                .orElseGet(() -> tierRepository.findFirstByActiveTrueOrderByTierLevelAsc()
                        .orElseThrow(() -> new MembershipException("No active tiers found")));
    }

    private UserMembership fetchActiveMembership(Long membershipId) {
        UserMembership membership = membershipRepository.findByIdWithUserAndTier(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("UserMembership", membershipId));

        membership = expireIfNeeded(membership);

        if (!MembershipStatus.ACTIVE.equals(membership.getStatus())) {
            throw new MembershipException(
                    "Membership " + membershipId + " is not active (status: " + membership.getStatus() + ")");
        }
        return membership;
    }
}
