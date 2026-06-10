package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.AddTierBenefitRequest;
import com.firstclub.membership.dto.request.AddTierCriteriaRequest;
import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierBenefitRequest;
import com.firstclub.membership.dto.request.UpdateTierCriteriaRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.TierBenefitConfigResponse;
import com.firstclub.membership.dto.response.TierEligibilityCriteriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MembershipTierService {

    Page<MembershipTierResponse> getActiveTiers(Pageable pageable);

    MembershipTierResponse getTier(Long tierId);

    MembershipTierResponse createTier(CreateTierRequest request);

    MembershipTierResponse updateTier(Long tierId, UpdateTierRequest request);

    void deactivateTier(Long tierId);

    List<TierBenefitConfigResponse> getTierBenefits(Long tierId);

    TierBenefitConfigResponse addBenefitToTier(Long tierId, AddTierBenefitRequest request);

    TierBenefitConfigResponse updateTierBenefit(Long tierId, Long configId, UpdateTierBenefitRequest request);

    void removeTierBenefit(Long tierId, Long configId);

    List<TierEligibilityCriteriaResponse> getTierCriteria(Long tierId);

    TierEligibilityCriteriaResponse addCriteriaToTier(Long tierId, AddTierCriteriaRequest request);

    TierEligibilityCriteriaResponse updateTierCriteria(Long tierId, Long criteriaId, UpdateTierCriteriaRequest request);

    void removeTierCriteria(Long tierId, Long criteriaId);
}
