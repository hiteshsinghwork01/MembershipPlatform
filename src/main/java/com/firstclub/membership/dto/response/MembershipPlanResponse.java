package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.PlanDurationType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MembershipPlanResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private PlanDurationType durationType;
    private int durationMonths;
    private boolean active;
}
