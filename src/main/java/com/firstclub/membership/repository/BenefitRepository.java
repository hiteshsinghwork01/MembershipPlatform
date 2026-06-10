package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Benefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    Page<Benefit> findByActiveTrueOrderByBenefitType(Pageable pageable);
}
