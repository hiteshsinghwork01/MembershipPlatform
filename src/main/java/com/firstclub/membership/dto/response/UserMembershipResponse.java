package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.MembershipStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserMembershipResponse {
    private Long id;
    private Long userId;
    private String userName;
    private MembershipPlanResponse plan;
    private MembershipTierResponse tier;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private boolean autoRenew;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDateTime lastRenewedAt;
}
