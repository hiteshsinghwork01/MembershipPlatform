package com.firstclub.membership.util;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.entity.MembershipPlan;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MembershipPlanUtil {

    public static MembershipPlan buildMembershipPlan(CreatePlanRequest request) {
        return MembershipPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationType(request.getDurationType())
                .active(true)
                .build();
    }

}
