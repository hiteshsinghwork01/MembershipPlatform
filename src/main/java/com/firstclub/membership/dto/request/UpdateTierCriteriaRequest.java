package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.CriteriaOperator;
import com.firstclub.membership.enums.EvaluationWindow;
import lombok.Getter;

@Getter
public class UpdateTierCriteriaRequest {

    private CriteriaOperator operator;

    private String criteriaValue;

    private EvaluationWindow evaluationWindow;

    private Boolean active;
}
