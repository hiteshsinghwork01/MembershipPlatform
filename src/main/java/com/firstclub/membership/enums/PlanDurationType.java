package com.firstclub.membership.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlanDurationType {
    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12);

    private final int months;
}
