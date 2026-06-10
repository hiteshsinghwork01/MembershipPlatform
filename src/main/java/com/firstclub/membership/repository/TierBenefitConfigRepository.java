package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierBenefitConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TierBenefitConfigRepository extends JpaRepository<TierBenefitConfig, Long> {

    List<TierBenefitConfig> findByMembershipTierIdOrderById(Long tierId);

    Optional<TierBenefitConfig> findByIdAndMembershipTierId(Long id, Long tierId);

    boolean existsByMembershipTierIdAndBenefitId(Long tierId, Long benefitId);
}
