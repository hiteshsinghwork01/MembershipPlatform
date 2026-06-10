package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.CriteriaOperator;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TierEligibilityCriteriaResponse {
    private Long id;
    private Long tierId;
    private CriteriaType criteriaType;
    private CriteriaOperator operator;
    private String criteriaValue;
    private EvaluationWindow evaluationWindow;
    private boolean active;
}
