package com.firstclub.membership.enums;

import java.math.BigDecimal;

public enum CriteriaOperator {
    GT {
        @Override
        public boolean apply(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) > 0;
        }
    },
    GTE {
        @Override
        public boolean apply(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) >= 0;
        }
    },
    EQ {
        @Override
        public boolean apply(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) == 0;
        }
    },
    LTE {
        @Override
        public boolean apply(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) <= 0;
        }
    };

    public abstract boolean apply(BigDecimal actual, BigDecimal threshold);
}
