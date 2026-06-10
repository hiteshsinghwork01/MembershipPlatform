package com.firstclub.membership.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdatePlanRequest {

    private String name;

    private String description;

    @DecimalMin(value = "0.01")
    private BigDecimal price;
}
