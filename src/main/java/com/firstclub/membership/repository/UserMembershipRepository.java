package com.firstclub.membership.repository;

import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {

    @EntityGraph(attributePaths = {"user", "membershipTier"})
    @Query("SELECT um FROM UserMembership um WHERE um.id = :id")
    Optional<UserMembership> findByIdWithUserAndTier(@Param("id") Long id);

    boolean existsByUser_IdAndStatus(long userId, MembershipStatus status);

    @EntityGraph(attributePaths = {"user", "membershipPlan", "membershipTier",
            "membershipTier.benefitConfigs", "membershipTier.benefitConfigs.benefit"})
    Optional<UserMembership> findByUser_IdAndStatus(long userId, MembershipStatus status);

    long countByMembershipTierIdAndStatus(Long tierId, MembershipStatus status);

    long countByMembershipPlanIdAndStatus(Long planId, MembershipStatus status);

    @EntityGraph(attributePaths = {"user", "membershipPlan", "membershipTier"})
    List<UserMembership> findByStatusAndAutoRenewTrueAndExpiryDateBefore(
            MembershipStatus status, LocalDateTime expiryBefore, Pageable pageable);
}
