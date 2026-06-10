package com.firstclub.membership.util;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.entity.Benefit;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BenefitUtil {

    public static Benefit buildBenefit(CreateBenefitRequest request) {
        return Benefit.builder()
                .name(request.getName())
                .description(request.getDescription())
                .benefitType(request.getBenefitType())
                .active(true)
                .build();
    }

}
