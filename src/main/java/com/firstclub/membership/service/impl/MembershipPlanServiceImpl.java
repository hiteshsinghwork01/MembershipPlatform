package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.MembershipPlanMapper;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.MembershipPlanService;
import com.firstclub.membership.util.MembershipPlanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final UserMembershipRepository userMembershipRepository;

    @Override
    public Page<MembershipPlanResponse> getActivePlans(Pageable pageable) {
        return planRepository.findByActiveTrueOrderByPriceAsc(pageable).map(MembershipPlanMapper::toResponse);
    }

    @Override
    public MembershipPlanResponse getPlan(Long planId) {
        return planRepository.findByIdAndActiveTrue(planId)
                .map(MembershipPlanMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", planId));
    }

    @Override
    public MembershipPlanResponse createPlan(CreatePlanRequest request) {
        return MembershipPlanMapper.toResponse(planRepository.save(MembershipPlanUtil.buildMembershipPlan(request)));
    }

    @Override
    public MembershipPlanResponse updatePlan(Long planId, UpdatePlanRequest request) {
        MembershipPlan plan = planRepository.findByIdAndActiveTrue(planId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", planId));

        if (request.getName() != null) {
            plan.setName(request.getName());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            plan.setPrice(request.getPrice());
        }

        return MembershipPlanMapper.toResponse(planRepository.save(plan));
    }

    @Override
    public void deactivatePlan(Long planId) {
        MembershipPlan plan = planRepository.findByIdAndActiveTrue(planId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", planId));
        long activeCount = userMembershipRepository.countByMembershipPlanIdAndStatus(planId, MembershipStatus.ACTIVE);
        if (activeCount > 0) {
            throw new MembershipException(
                    "Cannot deactivate plan with " + activeCount + " active member(s)");
        }
        plan.setActive(false);
        planRepository.save(plan);
    }
}
