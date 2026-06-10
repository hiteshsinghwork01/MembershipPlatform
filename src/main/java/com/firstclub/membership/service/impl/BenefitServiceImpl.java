package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.dto.request.UpdateBenefitRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.entity.Benefit;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.BenefitMapper;
import com.firstclub.membership.repository.BenefitRepository;
import com.firstclub.membership.service.BenefitService;
import com.firstclub.membership.util.BenefitUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final BenefitRepository benefitRepository;

    @Override
    public BenefitResponse createBenefit(CreateBenefitRequest request) {
        Benefit benefit = BenefitUtil.buildBenefit(request);
        return BenefitMapper.toResponse(benefitRepository.save(benefit));
    }

    @Override
    public BenefitResponse updateBenefit(Long benefitId, UpdateBenefitRequest request) {
        Benefit benefit = benefitRepository.findById(benefitId)
                .filter(Benefit::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit", benefitId));

        if (request.getName() != null) {
            benefit.setName(request.getName());
        }
        if (request.getDescription() != null) {
            benefit.setDescription(request.getDescription());
        }

        return BenefitMapper.toResponse(benefitRepository.save(benefit));
    }

    @Override
    public BenefitResponse getBenefit(Long benefitId) {
        return benefitRepository.findById(benefitId)
                .filter(Benefit::isActive)
                .map(BenefitMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit", benefitId));
    }

    @Override
    public Page<BenefitResponse> getAllBenefits(Pageable pageable) {
        return benefitRepository.findByActiveTrueOrderByBenefitType(pageable).map(BenefitMapper::toResponse);
    }

    @Override
    public void deactivateBenefit(Long benefitId) {
        Benefit benefit = benefitRepository.findById(benefitId)
                .filter(Benefit::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Benefit", benefitId));
        benefit.setActive(false);
        benefitRepository.save(benefit);
    }
}
