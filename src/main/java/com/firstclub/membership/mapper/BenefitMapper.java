package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.entity.Benefit;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BenefitMapper {

    public static BenefitResponse toResponse(Benefit benefit) {
        return BenefitResponse.builder()
                .id(benefit.getId())
                .name(benefit.getName())
                .description(benefit.getDescription())
                .benefitType(benefit.getBenefitType())
                .build();
    }
}
