package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.UserMembershipResponse;
import com.firstclub.membership.entity.UserMembership;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public final class UserMembershipMapper {

    public static UserMembershipResponse toResponse(UserMembership membership) {
        return UserMembershipResponse.builder()
                .id(membership.getId())
                .userId(membership.getUser().getId())
                .userName(membership.getUser().getName())
                .plan(MembershipPlanMapper.toResponse(membership.getMembershipPlan()))
                .tier(MembershipTierMapper.toResponse(membership.getMembershipTier()))
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .expiryDate(membership.getExpiryDate())
                .autoRenew(membership.isAutoRenew())
                .cancelledAt(membership.getCancelledAt())
                .cancellationReason(membership.getCancellationReason())
                .lastRenewedAt(membership.getLastRenewedAt())
                .build();
    }

}
