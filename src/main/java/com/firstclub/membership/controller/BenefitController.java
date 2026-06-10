package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.dto.request.UpdateBenefitRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.service.BenefitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final BenefitService benefitService;

    @PostMapping
    public ResponseEntity<ApiResponse<BenefitResponse>> createBenefit(@Valid @RequestBody CreateBenefitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(benefitService.createBenefit(request)));
    }

    @PutMapping("/{benefitId}")
    public ResponseEntity<ApiResponse<BenefitResponse>> updateBenefit(
            @PathVariable Long benefitId,
            @Valid @RequestBody UpdateBenefitRequest request) {
        return ResponseEntity.ok(ApiResponse.success(benefitService.updateBenefit(benefitId, request)));
    }

    @GetMapping("/{benefitId}")
    public ResponseEntity<ApiResponse<BenefitResponse>> getBenefit(@PathVariable Long benefitId) {
        return ResponseEntity.ok(ApiResponse.success(benefitService.getBenefit(benefitId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BenefitResponse>>> getBenefits(
            @PageableDefault(size = 20, sort = "benefitType") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(benefitService.getAllBenefits(pageable)));
    }

    @DeleteMapping("/{benefitId}")
    public ResponseEntity<ApiResponse<Void>> deactivateBenefit(@PathVariable Long benefitId) {
        benefitService.deactivateBenefit(benefitId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
