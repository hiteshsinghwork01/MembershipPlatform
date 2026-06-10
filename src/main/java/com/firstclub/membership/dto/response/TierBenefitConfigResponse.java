package com.firstclub.membership.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TierBenefitConfigResponse {
    private Long id;
    private BenefitResponse benefit;
    private BigDecimal discountPercentage;
    private String metadata;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}
