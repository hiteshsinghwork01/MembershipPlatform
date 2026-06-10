package com.firstclub.membership.entity;

import com.firstclub.membership.enums.CriteriaOperator;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.EvaluationWindow;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tier_eligibility_criteria",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_criteria_tier_type",
                columnNames = {"membership_tier_id", "criteria_type", "evaluation_window", "criteria_value"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierEligibilityCriteria extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_tier_id", nullable = false)
    private MembershipTier membershipTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_type", nullable = false, length = 30)
    private CriteriaType criteriaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 10)
    private CriteriaOperator operator;

    @Column(name = "criteria_value", nullable = false, length = 100)
    private String criteriaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_window", nullable = false, length = 20)
    private EvaluationWindow evaluationWindow;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
