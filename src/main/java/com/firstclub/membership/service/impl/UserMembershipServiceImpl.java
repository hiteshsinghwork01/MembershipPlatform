package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.TierTransitionResult;
import com.firstclub.membership.dto.request.CancelMembershipRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.MembershipChangeHistoryResponse;
import com.firstclub.membership.dto.response.MembershipUpgradeResponse;
import com.firstclub.membership.dto.response.UserMembershipResponse;
import com.firstclub.membership.entity.MembershipChangeHistory;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.MembershipChangeHistoryMapper;
import com.firstclub.membership.mapper.UserMembershipMapper;
import com.firstclub.membership.repository.MembershipChangeHistoryRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.AuditService;
import com.firstclub.membership.service.MembershipLifecycleService;
import com.firstclub.membership.service.UserMembershipService;
import com.firstclub.membership.util.ChangeHistoryUtil;
import com.firstclub.membership.util.UserMembershipUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMembershipServiceImpl implements UserMembershipService {

    private final UserMembershipRepository membershipRepository;
    private final MembershipChangeHistoryRepository changeHistoryRepository;
    private final MembershipLifecycleService membershipLifecycleService;
    private final AuditService<MembershipChangeHistory> auditService;

    @Override
    public UserMembershipResponse subscribe(SubscribeRequest request) {
        var saved = membershipLifecycleService.createSubscription(request);
        auditService.record(ChangeHistoryUtil.forSubscription(saved));
        return UserMembershipMapper.toResponse(saved);
    }

    @Override
    public MembershipUpgradeResponse upgradeTier(Long membershipId) {
        TierTransitionResult result = membershipLifecycleService.evaluateAndApplyUpgrade(membershipId);
        if (result.isTierChanged()) {
            auditService.record(ChangeHistoryUtil.forUpgrade(
                    result.getMembership(), result.getFromTier(), result.getToTier()));
        }
        return UserMembershipUtil.buildMembershipUpgradeResponse(
                result.getUser().getId(),
                result.getMembership(),
                result.isTierChanged(),
                result.isTierChanged()
                        ? "Tier upgraded to " + result.getToTier().getName()
                        : "Not eligible for upgrade");
    }

    @Override
    public UserMembershipResponse downgradeTier(Long membershipId) {
        TierTransitionResult result = membershipLifecycleService.applyDowngrade(membershipId);
        if (result.isTierChanged()) {
            auditService.record(ChangeHistoryUtil.forDowngrade(
                    result.getMembership(), result.getFromTier(), result.getToTier()));
        }
        return UserMembershipMapper.toResponse(result.getMembership());
    }

    @Override
    public UserMembershipResponse unsubscribe(Long membershipId, CancelMembershipRequest request) {
        var cancelled = membershipLifecycleService.cancel(membershipId, request);
        auditService.record(ChangeHistoryUtil.forCancellation(cancelled));
        return UserMembershipMapper.toResponse(cancelled);
    }

    @Override
    public UserMembershipResponse getActiveMembership(Long userId) {
        return membershipRepository.findByUser_IdAndStatus(userId, MembershipStatus.ACTIVE)
                .map(UserMembershipMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active membership found for user: " + userId));
    }

    @Override
    public Page<MembershipChangeHistoryResponse> getChangeHistory(Long membershipId, Pageable pageable) {
        if (!membershipRepository.existsById(membershipId)) {
            throw new ResourceNotFoundException("UserMembership", membershipId);
        }
        return changeHistoryRepository
                .findByUserMembershipIdOrderByChangedAtDesc(membershipId, pageable)
                .map(MembershipChangeHistoryMapper::toResponse);
    }
}
