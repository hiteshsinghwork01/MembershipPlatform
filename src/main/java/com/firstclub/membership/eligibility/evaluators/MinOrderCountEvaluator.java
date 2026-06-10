package com.firstclub.membership.eligibility.evaluators;

import com.firstclub.membership.eligibility.CriteriaEvaluator;
import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MinOrderCountEvaluator implements CriteriaEvaluator {

    @Override
    public String getSupportedType() {
        return CriteriaType.MIN_ORDER_COUNT.name();
    }

    @Override
    public boolean evaluate(TierEligibilityCriteria criteria, EligibilityContext context) {
        BigDecimal threshold = new BigDecimal(criteria.getCriteriaValue());
        BigDecimal actual = BigDecimal.valueOf(context.getOrderCount());
        return criteria.getOperator().apply(actual, threshold);
    }
}
