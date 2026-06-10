package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.AddTierBenefitRequest;
import com.firstclub.membership.dto.request.AddTierCriteriaRequest;
import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierBenefitRequest;
import com.firstclub.membership.dto.request.UpdateTierCriteriaRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.TierBenefitConfigResponse;
import com.firstclub.membership.dto.response.TierEligibilityCriteriaResponse;
import com.firstclub.membership.entity.Benefit;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefitConfig;
import com.firstclub.membership.entity.TierEligibilityCriteria;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.MembershipTierMapper;
import com.firstclub.membership.mapper.TierEligibilityMapper;
import com.firstclub.membership.repository.BenefitRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierBenefitConfigRepository;
import com.firstclub.membership.repository.TierEligibilityCriteriaRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.MembershipTierService;
import com.firstclub.membership.util.MembershipTierUtil;
import com.firstclub.membership.util.TierBenefitUtil;
import com.firstclub.membership.util.TierEligibilityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipTierServiceImpl implements MembershipTierService {

    private final MembershipTierRepository tierRepository;
    private final BenefitRepository benefitRepository;
    private final TierBenefitConfigRepository benefitConfigRepository;
    private final TierEligibilityCriteriaRepository criteriaRepository;
    private final UserMembershipRepository userMembershipRepository;

    @Override
    public Page<MembershipTierResponse> getActiveTiers(Pageable pageable) {
        return tierRepository.findByActiveTrueOrderByTierLevelAsc(pageable).map(MembershipTierMapper::toResponse);
    }

    @Override
    public MembershipTierResponse getTier(Long tierId) {
        return tierRepository.findByIdAndActiveTrue(tierId)
                .map(MembershipTierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", tierId));
    }

    @Override
    public MembershipTierResponse createTier(CreateTierRequest request) {
        MembershipTier tier = MembershipTierUtil.buildMembershipTier(request);
        return MembershipTierMapper.toResponse(tierRepository.save(tier));
    }

    @Override
    public MembershipTierResponse updateTier(Long tierId, UpdateTierRequest request) {
        MembershipTier tier = tierRepository.findByIdAndActiveTrue(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", tierId));
        MembershipTierUtil.updateMembershipTier(request, tier);
        return MembershipTierMapper.toResponse(tierRepository.save(tier));
    }

    @Override
    public void deactivateTier(Long tierId) {
        MembershipTier tier = tierRepository.findByIdAndActiveTrue(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", tierId));
        long activeCount = userMembershipRepository.countByMembershipTierIdAndStatus(tierId, MembershipStatus.ACTIVE);
        if (activeCount > 0) {
            throw new MembershipException(
                    "Cannot deactivate tier with " + activeCount + " active member(s)");
        }
        tier.setActive(false);
        tierRepository.save(tier);
    }

    @Override
    public List<TierBenefitConfigResponse> getTierBenefits(Long tierId) {
        requireActiveTier(tierId);
        return benefitConfigRepository.findByMembershipTierIdOrderById(tierId)
                .stream()
                .map(MembershipTierMapper::toBenefitConfigResponse)
                .toList();
    }

    @Override
    @Transactional
    public TierBenefitConfigResponse addBenefitToTier(Long tierId, AddTierBenefitRequest request) {
        MembershipTier tier = requireActiveTier(tierId);

        if (benefitConfigRepository.existsByMembershipTierIdAndBenefitId(tierId, request.getBenefitId())) {
            throw new MembershipException("Benefit " + request.getBenefitId() + " is already configured on tier "
                    + tierId);
        }

        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new ResourceNotFoundException("Benefit", request.getBenefitId()));
        TierBenefitConfig config = TierBenefitUtil.build(tier, benefit, request);

        return MembershipTierMapper.toBenefitConfigResponse(benefitConfigRepository.save(config));
    }

    @Override
    public TierBenefitConfigResponse updateTierBenefit(Long tierId, Long configId, UpdateTierBenefitRequest request) {
        TierBenefitConfig config = benefitConfigRepository.findByIdAndMembershipTierId(configId, tierId)
                .orElseThrow(() -> new ResourceNotFoundException("TierBenefitConfig", configId));

        TierBenefitUtil.updateTierBenefit(request, config);
        return MembershipTierMapper.toBenefitConfigResponse(benefitConfigRepository.save(config));
    }

    @Override
    public void removeTierBenefit(Long tierId, Long configId) {
        TierBenefitConfig config = benefitConfigRepository.findByIdAndMembershipTierId(configId, tierId)
                .orElseThrow(() -> new ResourceNotFoundException("TierBenefitConfig", configId));
        benefitConfigRepository.delete(config);
    }

    @Override
    public List<TierEligibilityCriteriaResponse> getTierCriteria(Long tierId) {
        requireActiveTier(tierId);
        return criteriaRepository.findByMembershipTierIdOrderById(tierId)
                .stream()
                .map(TierEligibilityMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TierEligibilityCriteriaResponse addCriteriaToTier(Long tierId, AddTierCriteriaRequest request) {
        MembershipTier tier = requireActiveTier(tierId);

        if (criteriaRepository.existsByMembershipTierIdAndCriteriaTypeAndEvaluationWindowAndCriteriaValue(
                tierId, request.getCriteriaType(), request.getEvaluationWindow(), request.getCriteriaValue())) {
            throw new MembershipException(
                    "Tier " + tierId + " already has a " + request.getCriteriaType()
                            + " criterion for window " + request.getEvaluationWindow()
                            + " with value " + request.getCriteriaValue());
        }

        TierEligibilityCriteria criteria = TierEligibilityUtil.build(request, tier);
        return TierEligibilityMapper.toResponse(criteriaRepository.save(criteria));
    }

    @Override
    public TierEligibilityCriteriaResponse updateTierCriteria(Long tierId, Long criteriaId, UpdateTierCriteriaRequest request) {
        TierEligibilityCriteria criteria = criteriaRepository.findByIdAndMembershipTierId(criteriaId, tierId)
                .orElseThrow(() -> new ResourceNotFoundException("TierEligibilityCriteria", criteriaId));

        TierEligibilityUtil.updateTierEligibilityCriteria(criteria, request);
        return TierEligibilityMapper.toResponse(criteriaRepository.save(criteria));
    }

    @Override
    public void removeTierCriteria(Long tierId, Long criteriaId) {
        TierEligibilityCriteria criteria = criteriaRepository.findByIdAndMembershipTierId(criteriaId, tierId)
                .orElseThrow(() -> new ResourceNotFoundException("TierEligibilityCriteria", criteriaId));
        criteriaRepository.delete(criteria);
    }

    private MembershipTier requireActiveTier(Long tierId) {
        return tierRepository.findByIdAndActiveTrue(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", tierId));
    }

}
