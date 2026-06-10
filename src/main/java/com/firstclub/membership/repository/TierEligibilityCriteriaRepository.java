package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TierEligibilityCriteriaRepository extends JpaRepository<TierEligibilityCriteria, Long> {

    List<TierEligibilityCriteria> findByMembershipTierIdOrderById(Long tierId);

    Optional<TierEligibilityCriteria> findByIdAndMembershipTierId(Long id, Long tierId);

    boolean existsByMembershipTierIdAndCriteriaTypeAndEvaluationWindowAndCriteriaValue(
            Long tierId, CriteriaType criteriaType, EvaluationWindow evaluationWindow, String criteriaValue);
}
