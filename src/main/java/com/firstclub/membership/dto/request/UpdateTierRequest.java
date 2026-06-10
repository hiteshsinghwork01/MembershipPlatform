package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.CriteriaLogic;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UpdateTierRequest {

    private String name;

    private String description;

    @Min(1)
    private Integer tierLevel;

    private CriteriaLogic criteriaLogic;
}
