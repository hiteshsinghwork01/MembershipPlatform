package com.firstclub.membership.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class MembershipDateUtils {

    public static LocalDateTime calculateExpiryDate(LocalDateTime startDate, int durationMonths) {
        return startDate.plusMonths(durationMonths);
    }

    public static boolean isExpired(LocalDateTime expiryDate) {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public static long daysBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return 0;
        }
        return java.time.Duration.between(from, to).toDays();
    }
}
