package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.dto.request.UpdateBenefitRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BenefitService {

    BenefitResponse createBenefit(CreateBenefitRequest request);

    BenefitResponse updateBenefit(Long benefitId, UpdateBenefitRequest request);

    BenefitResponse getBenefit(Long benefitId);

    Page<BenefitResponse> getAllBenefits(Pageable pageable);

    void deactivateBenefit(Long benefitId);
}
