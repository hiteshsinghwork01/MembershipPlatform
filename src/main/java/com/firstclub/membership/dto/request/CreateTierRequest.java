package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.CriteriaLogic;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateTierRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(1)
    private int tierLevel;

    @NotNull
    private CriteriaLogic criteriaLogic;
}
