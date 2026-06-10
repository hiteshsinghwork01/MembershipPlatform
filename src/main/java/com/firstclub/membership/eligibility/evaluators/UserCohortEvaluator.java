package com.firstclub.membership.eligibility.evaluators;

import com.firstclub.membership.eligibility.CriteriaEvaluator;
import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaType;
import org.springframework.stereotype.Component;

@Component
public class UserCohortEvaluator implements CriteriaEvaluator {

    @Override
    public String getSupportedType() {
        return CriteriaType.USER_COHORT.name();
    }

    @Override
    public boolean evaluate(TierEligibilityCriteria criteria, EligibilityContext context) {
        if (context.getUserCohort() == null) {
            return false;
        }
        String value = criteria.getCriteriaValue();
        return value != null && value.trim().equalsIgnoreCase(context.getUserCohort().name());
    }
}
