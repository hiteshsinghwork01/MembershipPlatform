package com.firstclub.membership.client;

import com.firstclub.membership.dto.response.OrderStatsResponse;
import com.firstclub.membership.enums.EvaluationWindow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class OrderClient {

    public OrderStatsResponse getOrderStats(Long userId, EvaluationWindow window) {
        int months = window.getMonths();
        // mock: replace with real HTTP call later
        long orderCount = ThreadLocalRandom.current().nextLong(1, 50);
        BigDecimal totalValue = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(500, 30000))
                .setScale(2, RoundingMode.HALF_UP);
        return OrderStatsResponse.builder()
                .orderCount(orderCount)
                .totalOrderValue(totalValue)
                .build();
    }
}
