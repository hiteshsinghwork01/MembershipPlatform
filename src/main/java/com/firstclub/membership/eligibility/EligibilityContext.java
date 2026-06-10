package com.firstclub.membership.eligibility;

import com.firstclub.membership.enums.UserCohort;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class EligibilityContext {
    private final Long userId;
    private final UserCohort userCohort;
    private final long orderCount;
    private final BigDecimal totalOrderValue;
    private final long accountAgeDays;
}
