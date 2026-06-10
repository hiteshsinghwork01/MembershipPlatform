package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.eligibility.TierEligibilityEngine;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.PlanDurationType;
import com.firstclub.membership.exception.AlreadySubscribedException;
import com.firstclub.membership.exception.InvalidTierTransitionException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.impl.MembershipLifecycleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipLifecycleServiceImplTest {

    @Mock private UserMembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;
    @Mock private MembershipPlanRepository planRepository;
    @Mock private MembershipTierRepository tierRepository;
    @Mock private TierEligibilityEngine eligibilityEngine;
    @Mock private AuditService<MembershipChangeHistory> auditService;

    @InjectMocks
    private MembershipLifecycleServiceImpl service;

    // ── expireIfNeeded ─────────────────────────────────────────────────────

    @Test
    void expireIfNeeded_doesNotExpireWhenNotYetExpired() {
        var membership = activeMembership(LocalDateTime.now().plusDays(10));
        var result = service.expireIfNeeded(membership);
        assertThat(result.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void expireIfNeeded_expiresAndAuditsWhenPastExpiryDate() {
        var membership = activeMembership(LocalDateTime.now().minusSeconds(1));
        when(membershipRepository.save(membership)).thenReturn(membership);

        var result = service.expireIfNeeded(membership);

        assertThat(result.getStatus()).isEqualTo(MembershipStatus.EXPIRED);
        verify(membershipRepository).save(membership);
        verify(auditService).record(any());
    }

    @Test
    void expireIfNeeded_isNoOpForAlreadyExpiredMembership() {
        var membership = membership(MembershipStatus.EXPIRED, LocalDateTime.now().minusDays(1));
        var result = service.expireIfNeeded(membership);
        assertThat(result.getStatus()).isEqualTo(MembershipStatus.EXPIRED);
        verify(membershipRepository, never()).save(any());
    }

    // ── createSubscription ─────────────────────────────────────────────────

    @Test
    void createSubscription_throwsWhenAlreadySubscribed() {
        var request = new SubscribeRequest();
        request.setUserId(1L);
        request.setPlanId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(membershipRepository.existsByUser_IdAndStatus(1L, MembershipStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> service.createSubscription(request))
                .isInstanceOf(AlreadySubscribedException.class);
    }

    // ── applyDowngrade ─────────────────────────────────────────────────────

    @Test
    void applyDowngrade_throwsWhenStillEligibleForCurrentTier() {
        var goldTier = tier(2);
        var membership = activeMembership(LocalDateTime.now().plusDays(30));
        membership.setMembershipTier(goldTier);

        when(membershipRepository.findByIdWithUserAndTier(1L)).thenReturn(Optional.of(membership));
        when(eligibilityEngine.resolveEligibleTier(any(EligibilityContext.class)))
                .thenReturn(Optional.of(goldTier));

        assertThatThrownBy(() -> service.applyDowngrade(1L))
                .isInstanceOf(InvalidTierTransitionException.class)
                .hasMessageContaining("still eligible");
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private UserMembership activeMembership(LocalDateTime expiryDate) {
        return membership(MembershipStatus.ACTIVE, expiryDate);
    }

    private UserMembership membership(MembershipStatus status, LocalDateTime expiryDate) {
        var plan = MembershipPlan.builder()
                .durationType(PlanDurationType.MONTHLY)
                .build();
        var tier = tier(1);
        return UserMembership.builder()
                .user(user())
                .membershipPlan(plan)
                .membershipTier(tier)
                .status(status)
                .startDate(LocalDateTime.now().minusDays(1))
                .expiryDate(expiryDate)
                .build();
    }

    private User user() {
        return User.builder()
                .name("Test User")
                .email("test@test.com")
                .active(true)
                .build();
    }

    private MembershipTier tier(int level) {
        return MembershipTier.builder()
                .tierLevel(level)
                .name("Tier " + level)
                .active(true)
                .build();
    }
}
