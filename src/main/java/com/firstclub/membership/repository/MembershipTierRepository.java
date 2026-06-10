package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {

    @EntityGraph(attributePaths = {"benefitConfigs", "benefitConfigs.benefit"})
    Optional<MembershipTier> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = {"benefitConfigs", "benefitConfigs.benefit"})
    Page<MembershipTier> findByActiveTrueOrderByTierLevelAsc(Pageable pageable);

    @EntityGraph(attributePaths = {"eligibilityCriteria"})
    List<MembershipTier> findByActiveTrueOrderByTierLevelDesc();

    @EntityGraph(attributePaths = {"benefitConfigs", "benefitConfigs.benefit"})
    Optional<MembershipTier> findByTierLevelAndActiveTrue(int tierLevel);

    @EntityGraph(attributePaths = {"benefitConfigs", "benefitConfigs.benefit"})
    Optional<MembershipTier> findFirstByActiveTrueOrderByTierLevelAsc();

    @EntityGraph(attributePaths = {"benefitConfigs", "benefitConfigs.benefit"})
    Optional<MembershipTier> findFirstByActiveTrueAndTierLevelLessThanOrderByTierLevelDesc(int tierLevel);

}
