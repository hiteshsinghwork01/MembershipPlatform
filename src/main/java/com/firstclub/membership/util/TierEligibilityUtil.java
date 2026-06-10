package com.firstclub.membership.util;

import com.firstclub.membership.dto.request.AddTierCriteriaRequest;
import com.firstclub.membership.dto.request.UpdateTierCriteriaRequest;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TierEligibilityUtil {

    public static TierEligibilityCriteria build(AddTierCriteriaRequest request, MembershipTier tier) {
        return TierEligibilityCriteria.builder()
                .membershipTier(tier)
                .criteriaType(request.getCriteriaType())
                .operator(request.getOperator())
                .criteriaValue(request.getCriteriaValue())
                .evaluationWindow(request.getEvaluationWindow())
                .active(true)
                .build();
    }

    public static void updateTierEligibilityCriteria(TierEligibilityCriteria criteria, UpdateTierCriteriaRequest request) {
        if (request.getOperator() != null) {
            criteria.setOperator(request.getOperator());
        }
        if (request.getCriteriaValue() != null) {
            criteria.setCriteriaValue(request.getCriteriaValue());
        }
        if (request.getEvaluationWindow() != null) {
            criteria.setEvaluationWindow(request.getEvaluationWindow());
        }
        if (request.getActive() != null) {
            criteria.setActive(request.getActive());
        }
    }
}
