package com.firstclub.membership.entity;

import com.firstclub.membership.enums.CriteriaLogic;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "tier_level", nullable = false, unique = true)
    private int tierLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_logic", nullable = false, length = 10)
    @Builder.Default
    private CriteriaLogic criteriaLogic = CriteriaLogic.OR;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "membershipTier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TierEligibilityCriteria> eligibilityCriteria = new ArrayList<>();

    @OneToMany(mappedBy = "membershipTier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TierBenefitConfig> benefitConfigs = new ArrayList<>();
}
