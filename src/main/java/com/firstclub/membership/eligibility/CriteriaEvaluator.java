package com.firstclub.membership.eligibility;

import com.firstclub.membership.entity.TierEligibilityCriteria;

public interface CriteriaEvaluator {

    String getSupportedType();

    boolean evaluate(TierEligibilityCriteria criteria, EligibilityContext context);
}
