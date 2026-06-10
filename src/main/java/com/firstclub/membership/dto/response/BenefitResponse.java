package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.BenefitType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BenefitResponse {
    private Long id;
    private String name;
    private String description;
    private BenefitType benefitType;
}
