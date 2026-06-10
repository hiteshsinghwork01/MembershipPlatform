package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CancelMembershipRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.*;
import com.firstclub.membership.service.UserMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class UserMembershipController {

    private final UserMembershipService membershipService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> subscribe(
            @Valid @RequestBody SubscribeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscribed successfully", membershipService.subscribe(request)));
    }

    @PutMapping("/{membershipId}/unsubscribe")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> unsubscribe(
            @PathVariable Long membershipId,
            @RequestBody(required = false) CancelMembershipRequest request) {
        if (request == null) request = new CancelMembershipRequest();
        return ResponseEntity.ok(ApiResponse.success(
                "Unsubscribed successfully", membershipService.unsubscribe(membershipId, request)));
    }

    @PutMapping("/{membershipId}/upgrade")
    public ResponseEntity<ApiResponse<MembershipUpgradeResponse>> upgradeTier(@PathVariable Long membershipId) {
        MembershipUpgradeResponse response = membershipService.upgradeTier(membershipId);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PutMapping("/{membershipId}/downgrade")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> downgradeTier(@PathVariable Long membershipId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tier downgraded successfully", membershipService.downgradeTier(membershipId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> getActiveMembership(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getActiveMembership(userId)));
    }

    @GetMapping("/{membershipId}/history")
    public ResponseEntity<ApiResponse<Page<MembershipChangeHistoryResponse>>> getChangeHistory(
            @PathVariable Long membershipId,
            @PageableDefault(size = 20, sort = "changedAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getChangeHistory(membershipId, pageable)));
    }

}
