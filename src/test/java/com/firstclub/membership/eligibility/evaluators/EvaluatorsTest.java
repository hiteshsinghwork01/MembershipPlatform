package com.firstclub.membership.eligibility.evaluators;

import com.firstclub.membership.eligibility.EligibilityContext;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaOperator;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import com.firstclub.membership.enums.UserCohort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluatorsTest {

    // ── MinOrderCountEvaluator ─────────────────────────────────────────────

    @Test
    void minOrderCount_passesWhenAtThreshold() {
        var evaluator = new MinOrderCountEvaluator();
        var criteria = criteria(CriteriaType.MIN_ORDER_COUNT, CriteriaOperator.GTE, "10");
        var ctx = contextWithOrders(10, BigDecimal.ZERO);
        assertThat(evaluator.evaluate(criteria, ctx)).isTrue();
    }

    @Test
    void minOrderCount_failsWhenBelowThreshold() {
        var evaluator = new MinOrderCountEvaluator();
        var criteria = criteria(CriteriaType.MIN_ORDER_COUNT, CriteriaOperator.GTE, "10");
        var ctx = contextWithOrders(9, BigDecimal.ZERO);
        assertThat(evaluator.evaluate(criteria, ctx)).isFalse();
    }

    // ── MinOrderValueEvaluator ─────────────────────────────────────────────

    @Test
    void minOrderValue_passesWhenAboveThreshold() {
        var evaluator = new MinOrderValueEvaluator();
        var criteria = criteria(CriteriaType.MIN_ORDER_VALUE, CriteriaOperator.GTE, "5000.00");
        var ctx = contextWithOrders(1, new BigDecimal("6000.00"));
        assertThat(evaluator.evaluate(criteria, ctx)).isTrue();
    }

    @Test
    void minOrderValue_failsWhenNullTotalOrderValueTreatedAsZero() {
        var evaluator = new MinOrderValueEvaluator();
        var criteria = criteria(CriteriaType.MIN_ORDER_VALUE, CriteriaOperator.GTE, "5000.00");
        var ctx = EligibilityContext.builder().userId(1L).orderCount(1).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isFalse();
    }

    // ── UserCohortEvaluator ────────────────────────────────────────────────
    // Each criteria row carries a single cohort value (EQ operator).
    // Multiple cohorts are expressed as multiple rows combined by the tier's OR logic.

    @Test
    void userCohort_passesWhenCohortMatches() {
        var evaluator = new UserCohortEvaluator();
        var criteria = criteria(CriteriaType.USER_COHORT, CriteriaOperator.EQ, "VIP");
        var ctx = EligibilityContext.builder().userId(1L).userCohort(UserCohort.VIP).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isTrue();
    }

    @Test
    void userCohort_failsWhenCohortDoesNotMatch() {
        var evaluator = new UserCohortEvaluator();
        var criteria = criteria(CriteriaType.USER_COHORT, CriteriaOperator.EQ, "VIP");
        var ctx = EligibilityContext.builder().userId(1L).userCohort(UserCohort.NEW).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isFalse();
    }

    @Test
    void userCohort_failsWhenCohortIsNull() {
        var evaluator = new UserCohortEvaluator();
        var criteria = criteria(CriteriaType.USER_COHORT, CriteriaOperator.EQ, "VIP");
        var ctx = EligibilityContext.builder().userId(1L).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isFalse();
    }

    @Test
    void userCohort_matchIsCaseInsensitive() {
        var evaluator = new UserCohortEvaluator();
        var criteria = criteria(CriteriaType.USER_COHORT, CriteriaOperator.EQ, "vip");
        var ctx = EligibilityContext.builder().userId(1L).userCohort(UserCohort.VIP).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isTrue();
    }

    // ── TenureDaysEvaluator ────────────────────────────────────────────────

    @Test
    void tenureDays_passesWhenAboveThreshold() {
        var evaluator = new TenureDaysEvaluator();
        var criteria = criteria(CriteriaType.TENURE_DAYS, CriteriaOperator.GTE, "30");
        var ctx = EligibilityContext.builder().userId(1L).accountAgeDays(31).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isTrue();
    }

    @Test
    void tenureDays_failsWhenBelowThreshold() {
        var evaluator = new TenureDaysEvaluator();
        var criteria = criteria(CriteriaType.TENURE_DAYS, CriteriaOperator.GTE, "30");
        var ctx = EligibilityContext.builder().userId(1L).accountAgeDays(10).build();
        assertThat(evaluator.evaluate(criteria, ctx)).isFalse();
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private TierEligibilityCriteria criteria(CriteriaType type, CriteriaOperator operator, String value) {
        return TierEligibilityCriteria.builder()
                .criteriaType(type)
                .operator(operator)
                .criteriaValue(value)
                .evaluationWindow(EvaluationWindow.ALL_TIME)
                .active(true)
                .build();
    }

    private EligibilityContext contextWithOrders(long count, BigDecimal totalValue) {
        return EligibilityContext.builder()
                .userId(1L)
                .orderCount(count)
                .totalOrderValue(totalValue)
                .build();
    }
}
