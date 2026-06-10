package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.TierEligibilityCriteriaResponse;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TierEligibilityMapper {

    public static TierEligibilityCriteriaResponse toResponse(TierEligibilityCriteria criteria) {
        return TierEligibilityCriteriaResponse.builder()
                .id(criteria.getId())
                .tierId(criteria.getMembershipTier().getId())
                .criteriaType(criteria.getCriteriaType())
                .operator(criteria.getOperator())
                .criteriaValue(criteria.getCriteriaValue())
                .evaluationWindow(criteria.getEvaluationWindow())
                .active(criteria.isActive())
                .build();
    }

}
