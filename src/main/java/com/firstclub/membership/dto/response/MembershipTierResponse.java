package com.firstclub.membership.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MembershipTierResponse {
    private Long id;
    private String name;
    private String description;
    private int tierLevel;
    private List<TierBenefitConfigResponse> benefits;
}
