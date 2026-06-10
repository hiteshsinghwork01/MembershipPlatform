package com.firstclub.membership.eligibility;

import com.firstclub.membership.client.OrderClient;
import com.firstclub.membership.dto.response.OrderStatsResponse;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaLogic;
import com.firstclub.membership.enums.CriteriaOperator;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import com.firstclub.membership.repository.MembershipTierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierEligibilityEngineTest {

    @Mock private MembershipTierRepository tierRepository;
    @Mock private CriteriaEvaluatorRegistry registry;
    @Mock private OrderClient orderClient;

    @InjectMocks
    private TierEligibilityEngine engine;

    private EligibilityContext baseContext;

    @BeforeEach
    void setUp() {
        baseContext = EligibilityContext.builder().userId(1L).build();
    }

    @Test
    void returnsEmptyWhenNoActiveTiers() {
        when(tierRepository.findByActiveTrueOrderByTierLevelDesc()).thenReturn(List.of());
        assertThat(engine.resolveEligibleTier(baseContext)).isEmpty();
    }

    @Test
    void returnsHighestEligibleTierInDescendingOrder() {
        var silver = tierWithOrderCountCriteria(1, CriteriaLogic.OR, 1);
        var gold   = tierWithOrderCountCriteria(2, CriteriaLogic.OR, 10);

        when(tierRepository.findByActiveTrueOrderByTierLevelDesc()).thenReturn(List.of(gold, silver));
        when(registry.get(CriteriaType.MIN_ORDER_COUNT.name())).thenReturn(thresholdEvaluator());
        when(orderClient.getOrderStats(anyLong(), any())).thenReturn(
                OrderStatsResponse.builder().orderCount(5).totalOrderValue(BigDecimal.ZERO).build());

        Optional<MembershipTier> result = engine.resolveEligibleTier(baseContext);
        assertThat(result).isPresent();
        assertThat(result.get().getTierLevel()).isEqualTo(1);
    }

    @Test
    void orLogic_eligibleWhenAnyCriteriaPasses() {
        TierEligibilityCriteria passing = orderCountCriteria(1);
        TierEligibilityCriteria failing = orderCountCriteria(100);
        var tier = tier(1, CriteriaLogic.OR, List.of(failing, passing));

        when(tierRepository.findByActiveTrueOrderByTierLevelDesc()).thenReturn(List.of(tier));
        when(registry.get(CriteriaType.MIN_ORDER_COUNT.name())).thenReturn(thresholdEvaluator());
        when(orderClient.getOrderStats(anyLong(), any())).thenReturn(
                OrderStatsResponse.builder().orderCount(5).totalOrderValue(BigDecimal.ZERO).build());

        assertThat(engine.resolveEligibleTier(baseContext)).isPresent();
    }

    @Test
    void andLogic_ineligibleWhenAnyCriteriaFails() {
        TierEligibilityCriteria passing = orderCountCriteria(1);
        TierEligibilityCriteria failing = orderCountCriteria(100);
        var tier = tier(1, CriteriaLogic.AND, List.of(passing, failing));

        when(tierRepository.findByActiveTrueOrderByTierLevelDesc()).thenReturn(List.of(tier));
        when(registry.get(CriteriaType.MIN_ORDER_COUNT.name())).thenReturn(thresholdEvaluator());
        when(orderClient.getOrderStats(anyLong(), any())).thenReturn(
                OrderStatsResponse.builder().orderCount(5).totalOrderValue(BigDecimal.ZERO).build());

        assertThat(engine.resolveEligibleTier(baseContext)).isEmpty();
    }

    @Test
    void doesNotCallOrderClientForUserCohortCriteria() {
        TierEligibilityCriteria cohortCriteria = TierEligibilityCriteria.builder()
                .criteriaType(CriteriaType.USER_COHORT)
                .operator(CriteriaOperator.EQ)
                .criteriaValue("VIP")
                .evaluationWindow(EvaluationWindow.ALL_TIME)
                .active(true)
                .build();
        var tier = tier(1, CriteriaLogic.OR, List.of(cohortCriteria));

        when(tierRepository.findByActiveTrueOrderByTierLevelDesc()).thenReturn(List.of(tier));
        when(registry.get(CriteriaType.USER_COHORT.name())).thenReturn(alwaysFailEvaluator());

        engine.resolveEligibleTier(baseContext);

        verify(orderClient, never()).getOrderStats(anyLong(), any());
    }

    @Test
    void tierWithNoCriteriaIsAlwaysEligible() {
        var tier = tier(1, CriteriaLogic.OR, List.of());
        when(tierRepository.findByActiveTrueOrderByTierLevelDesc()).thenReturn(List.of(tier));

        assertThat(engine.resolveEligibleTier(baseContext)).isPresent();
        verify(orderClient, never()).getOrderStats(anyLong(), any());
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private MembershipTier tierWithOrderCountCriteria(int level, CriteriaLogic logic, int minCount) {
        return tier(level, logic, List.of(orderCountCriteria(minCount)));
    }

    private MembershipTier tier(int level, CriteriaLogic logic, List<TierEligibilityCriteria> criteria) {
        var tier = MembershipTier.builder()
                .tierLevel(level)
                .criteriaLogic(logic)
                .active(true)
                .build();
        criteria.forEach(c -> {
            tier.getEligibilityCriteria().add(c);
            c.setMembershipTier(tier);
        });
        return tier;
    }

    private TierEligibilityCriteria orderCountCriteria(int threshold) {
        return TierEligibilityCriteria.builder()
                .criteriaType(CriteriaType.MIN_ORDER_COUNT)
                .operator(CriteriaOperator.GTE)
                .criteriaValue(String.valueOf(threshold))
                .evaluationWindow(EvaluationWindow.MONTHLY)
                .active(true)
                .build();
    }

    /** Evaluator that passes when context.orderCount >= criteria value (numeric string). */
    private CriteriaEvaluator thresholdEvaluator() {
        return new CriteriaEvaluator() {
            @Override
            public String getSupportedType() { return CriteriaType.MIN_ORDER_COUNT.name(); }
            @Override
            public boolean evaluate(TierEligibilityCriteria c, EligibilityContext ctx) {
                return ctx.getOrderCount() >= Long.parseLong(c.getCriteriaValue());
            }
        };
    }

    private CriteriaEvaluator alwaysFailEvaluator() {
        return new CriteriaEvaluator() {
            @Override
            public String getSupportedType() { return ""; }
            @Override
            public boolean evaluate(TierEligibilityCriteria c, EligibilityContext ctx) { return false; }
        };
    }
}
