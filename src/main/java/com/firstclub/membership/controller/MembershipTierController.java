package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.AddTierBenefitRequest;
import com.firstclub.membership.dto.request.AddTierCriteriaRequest;
import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierBenefitRequest;
import com.firstclub.membership.dto.request.UpdateTierCriteriaRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.TierBenefitConfigResponse;
import com.firstclub.membership.dto.response.TierEligibilityCriteriaResponse;
import com.firstclub.membership.service.MembershipTierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
public class MembershipTierController {

    private final MembershipTierService tierService;

    @PostMapping
    public ResponseEntity<ApiResponse<MembershipTierResponse>> createTier(
            @Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tierService.createTier(request)));
    }

    @PutMapping("/{tierId}")
    public ResponseEntity<ApiResponse<MembershipTierResponse>> updateTier(
            @PathVariable long tierId,
            @Valid @RequestBody UpdateTierRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tierService.updateTier(tierId, request)));
    }

    @GetMapping("/{tierId}")
    public ResponseEntity<ApiResponse<MembershipTierResponse>> getTier(@PathVariable long tierId) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getTier(tierId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MembershipTierResponse>>> getTiers(
            @PageableDefault(size = 20, sort = "tierLevel") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getActiveTiers(pageable)));
    }

    @DeleteMapping("/{tierId}")
    public ResponseEntity<ApiResponse<Void>> deactivateTier(@PathVariable long tierId) {
        tierService.deactivateTier(tierId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{tierId}/benefits")
    public ResponseEntity<ApiResponse<TierBenefitConfigResponse>> addBenefit(
            @PathVariable Long tierId,
            @Valid @RequestBody AddTierBenefitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tierService.addBenefitToTier(tierId, request)));
    }

    @PutMapping("/{tierId}/benefits/{configId}")
    public ResponseEntity<ApiResponse<TierBenefitConfigResponse>> updateBenefit(
            @PathVariable Long tierId,
            @PathVariable Long configId,
            @Valid @RequestBody UpdateTierBenefitRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tierService.updateTierBenefit(tierId, configId, request)));
    }

    @GetMapping("/{tierId}/benefits")
    public ResponseEntity<ApiResponse<List<TierBenefitConfigResponse>>> getTierBenefits(@PathVariable Long tierId) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getTierBenefits(tierId)));
    }

    @DeleteMapping("/{tierId}/benefits/{configId}")
    public ResponseEntity<ApiResponse<Void>> removeBenefit(
            @PathVariable Long tierId,
            @PathVariable Long configId) {
        tierService.removeTierBenefit(tierId, configId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{tierId}/criteria")
    public ResponseEntity<ApiResponse<TierEligibilityCriteriaResponse>> addCriteria(
            @PathVariable Long tierId,
            @Valid @RequestBody AddTierCriteriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tierService.addCriteriaToTier(tierId, request)));
    }

    @PutMapping("/{tierId}/criteria/{criteriaId}")
    public ResponseEntity<ApiResponse<TierEligibilityCriteriaResponse>> updateCriteria(
            @PathVariable Long tierId,
            @PathVariable Long criteriaId,
            @Valid @RequestBody UpdateTierCriteriaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tierService.updateTierCriteria(tierId, criteriaId, request)));
    }

    @GetMapping("/{tierId}/criteria")
    public ResponseEntity<ApiResponse<List<TierEligibilityCriteriaResponse>>> getTierCriteria(
            @PathVariable Long tierId) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getTierCriteria(tierId)));
    }

    @DeleteMapping("/{tierId}/criteria/{criteriaId}")
    public ResponseEntity<ApiResponse<Void>> removeCriteria(
            @PathVariable Long tierId,
            @PathVariable Long criteriaId) {
        tierService.removeTierCriteria(tierId, criteriaId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
