package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MembershipPlanService {

    Page<MembershipPlanResponse> getActivePlans(Pageable pageable);

    MembershipPlanResponse getPlan(Long planId);

    MembershipPlanResponse createPlan(CreatePlanRequest request);

    MembershipPlanResponse updatePlan(Long planId, UpdatePlanRequest request);

    void deactivatePlan(Long planId);
}
