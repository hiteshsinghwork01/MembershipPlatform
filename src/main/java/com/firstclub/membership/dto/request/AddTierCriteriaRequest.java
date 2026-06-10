package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.CriteriaOperator;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddTierCriteriaRequest {

    @NotNull
    private CriteriaType criteriaType;

    @NotNull
    private CriteriaOperator operator;

    @NotBlank
    private String criteriaValue;

    @NotNull
    private EvaluationWindow evaluationWindow;
}
