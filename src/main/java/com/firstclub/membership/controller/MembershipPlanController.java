package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;

    @PostMapping
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> createPlan(
            @Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(planService.createPlan(request)));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody UpdatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(planService.updatePlan(planId, request)));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> getPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(ApiResponse.success(planService.getPlan(planId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MembershipPlanResponse>>> getPlans(
            @PageableDefault(size = 20, sort = "price") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(planService.getActivePlans(pageable)));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> deactivatePlan(@PathVariable Long planId) {
        planService.deactivatePlan(planId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
