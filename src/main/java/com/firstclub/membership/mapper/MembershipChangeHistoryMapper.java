package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.MembershipChangeHistoryResponse;
import com.firstclub.membership.entity.MembershipChangeHistory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MembershipChangeHistoryMapper {

    public static MembershipChangeHistoryResponse toResponse(MembershipChangeHistory history) {
        return MembershipChangeHistoryResponse.builder()
                .id(history.getId())
                .changeType(history.getChangeType())
                .fromTierName(history.getFromTier() != null ? history.getFromTier().getName() : null)
                .toTierName(history.getToTier() != null ? history.getToTier().getName() : null)
                .reason(history.getReason())
                .changedAt(history.getChangedAt())
                .build();
    }
}
