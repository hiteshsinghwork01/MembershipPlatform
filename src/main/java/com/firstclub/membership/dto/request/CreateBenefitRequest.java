package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.BenefitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateBenefitRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BenefitType benefitType;
}
