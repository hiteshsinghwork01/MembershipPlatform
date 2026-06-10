package com.firstclub.membership.eligibility.evaluators;

import com.firstclub.membership.eligibility.CriteriaEvaluator;
import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MinOrderValueEvaluator implements CriteriaEvaluator {

    @Override
    public String getSupportedType() {
        return CriteriaType.MIN_ORDER_VALUE.name();
    }

    @Override
    public boolean evaluate(TierEligibilityCriteria criteria, EligibilityContext context) {
        BigDecimal threshold = new BigDecimal(criteria.getCriteriaValue());
        BigDecimal actual = context.getTotalOrderValue() != null
                ? context.getTotalOrderValue()
                : BigDecimal.ZERO;
        return criteria.getOperator().apply(actual, threshold);
    }
}
