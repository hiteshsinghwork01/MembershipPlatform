package com.firstclub.membership.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MembershipUpgradeResponse {
    private Long userId;
    private UserMembershipResponse membershipResponse;
    private boolean upgraded;
    private String message;
}
