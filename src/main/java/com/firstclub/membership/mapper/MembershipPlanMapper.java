package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.entity.MembershipPlan;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MembershipPlanMapper {

    public static MembershipPlanResponse toResponse(MembershipPlan plan) {
        return MembershipPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .durationType(plan.getDurationType())
                .durationMonths(plan.getDurationType().getMonths())
                .active(plan.isActive())
                .build();
    }
}
