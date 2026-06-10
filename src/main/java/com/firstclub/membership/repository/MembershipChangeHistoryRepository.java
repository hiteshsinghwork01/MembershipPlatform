package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipChangeHistoryRepository extends JpaRepository<MembershipChangeHistory, Long> {

    @EntityGraph(attributePaths = {"fromTier", "toTier"})
    Page<MembershipChangeHistory> findByUserMembershipIdOrderByChangedAtDesc(Long membershipId, Pageable pageable);
}
