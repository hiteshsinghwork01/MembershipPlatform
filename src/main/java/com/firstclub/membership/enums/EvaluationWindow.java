package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationWindow {
    MONTHLY(1),
    QUARTERLY(3),
    ALL_TIME(-1);

    private final int months;

}
