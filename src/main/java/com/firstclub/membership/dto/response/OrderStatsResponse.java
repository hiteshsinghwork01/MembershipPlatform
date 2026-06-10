package com.firstclub.membership.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderStatsResponse {

    private long orderCount;

    private BigDecimal totalOrderValue;

}