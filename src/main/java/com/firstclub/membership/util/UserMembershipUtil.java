package com.firstclub.membership.util;

import com.firstclub.membership.dto.TierTransitionResult;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.MembershipUpgradeResponse;
import com.firstclub.membership.dto.response.OrderStatsResponse;
import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.mapper.UserMembershipMapper;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class UserMembershipUtil {

    public static UserMembership buildUserMembership(User user, MembershipPlan plan, MembershipTier tier,
                                                     SubscribeRequest request) {
        LocalDateTime now = LocalDateTime.now();

        return UserMembership.builder()
                .user(user)
                .membershipPlan(plan)
                .membershipTier(tier)
                .status(MembershipStatus.ACTIVE)
                .startDate(now)
                .expiryDate(MembershipDateUtils.calculateExpiryDate(now, plan.getDurationType().getMonths()))
                .autoRenew(request.isAutoRenew())
                .paymentReference(request.getPaymentReference())
                .build();
    }

    public static EligibilityContext buildEligibilityContextWithoutOrderDetails(User user) {
        return EligibilityContext.builder()
                .userId(user.getId())
                .userCohort(user.getCohort())
                .accountAgeDays(MembershipDateUtils.daysBetween(user.getCreatedAt(), LocalDateTime.now()))
                .build();
    }

    public static MembershipUpgradeResponse buildMembershipUpgradeResponse(
            long userId, UserMembership userMembership, boolean upgraded, String message) {
        return MembershipUpgradeResponse.builder()
                .userId(userId)
                .membershipResponse(UserMembershipMapper.toResponse(userMembership))
                .upgraded(upgraded)
                .message(message)
                .build();
    }

    public static TierTransitionResult buildNoChangeTierTransition(User user, UserMembership membership) {
        return TierTransitionResult.builder()
                .user(user)
                .membership(membership)
                .tierChanged(false)
                .build();
    }

}
