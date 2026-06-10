package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.ChangeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MembershipChangeHistoryResponse {
    private Long id;
    private ChangeType changeType;
    private String fromTierName;
    private String toTierName;
    private String reason;
    private LocalDateTime changedAt;
}
