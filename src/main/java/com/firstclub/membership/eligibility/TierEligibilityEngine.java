package com.firstclub.membership.eligibility;

import com.firstclub.membership.client.OrderClient;
import com.firstclub.membership.dto.response.OrderStatsResponse;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaLogic;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import com.firstclub.membership.repository.MembershipTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TierEligibilityEngine {

    private final MembershipTierRepository tierRepository;
    private final CriteriaEvaluatorRegistry registry;
    private final OrderClient orderClient;

    public Optional<MembershipTier> resolveEligibleTier(EligibilityContext context) {
        List<MembershipTier> tiersDesc = tierRepository.findByActiveTrueOrderByTierLevelDesc();

        for (MembershipTier tier : tiersDesc) {
            if (isEligible(tier, context)) {
                return Optional.of(tier);
            }
        }
        return Optional.empty();
    }

    private boolean isEligible(MembershipTier tier, EligibilityContext context) {
        List<TierEligibilityCriteria> criteria = tier.getEligibilityCriteria().stream()
                .filter(TierEligibilityCriteria::isActive)
                .toList();

        if (criteria.isEmpty()) {
            return true;
        }

        Map<EvaluationWindow, EligibilityContext> contextByWindow = buildContextByWindow(context, criteria);

        if (CriteriaLogic.OR.equals(tier.getCriteriaLogic())) {
            return criteria.stream().anyMatch(c -> evaluate(c, contextByWindow.getOrDefault(c.getEvaluationWindow(), context)));
        } else {
            return criteria.stream().allMatch(c -> evaluate(c, contextByWindow.getOrDefault(c.getEvaluationWindow(), context)));
        }
    }

    private Map<EvaluationWindow, EligibilityContext> buildContextByWindow(EligibilityContext base, List<TierEligibilityCriteria> criteria) {
        return criteria.stream()
                .filter(c -> needsOrderStats(c.getCriteriaType()))
                .map(TierEligibilityCriteria::getEvaluationWindow)
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        window -> {
                            OrderStatsResponse stats = orderClient.getOrderStats(base.getUserId(), window);
                            return EligibilityContext.builder()
                                    .userId(base.getUserId())
                                    .userCohort(base.getUserCohort())
                                    .accountAgeDays(base.getAccountAgeDays())
                                    .orderCount(stats.getOrderCount())
                                    .totalOrderValue(stats.getTotalOrderValue())
                                    .build();
                        }
                ));
    }

    private boolean needsOrderStats(CriteriaType type) {
        return type == CriteriaType.MIN_ORDER_COUNT || type == CriteriaType.MIN_ORDER_VALUE;
    }

    private boolean evaluate(TierEligibilityCriteria criteria, EligibilityContext context) {
        try {
            return registry.get(criteria.getCriteriaType().name()).evaluate(criteria, context);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
