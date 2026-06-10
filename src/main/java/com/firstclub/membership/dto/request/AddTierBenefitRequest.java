package com.firstclub.membership.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AddTierBenefitRequest {

    @NotNull
    private Long benefitId;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal discountPercentage;

    private String metadata;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;
}
