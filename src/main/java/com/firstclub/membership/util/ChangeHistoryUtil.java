package com.firstclub.membership.util;

import com.firstclub.membership.entity.MembershipChangeHistory;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.ChangeType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ChangeHistoryUtil {

    public static MembershipChangeHistory forSubscription(UserMembership membership) {
        return MembershipChangeHistory.builder()
                .userMembership(membership)
                .changeType(ChangeType.SUBSCRIBED)
                .toTier(membership.getMembershipTier())
                .changedAt(LocalDateTime.now())
                .build();
    }

    public static MembershipChangeHistory forUpgrade(UserMembership membership,
                                                     MembershipTier fromTier,
                                                     MembershipTier toTier) {
        return MembershipChangeHistory.builder()
                .userMembership(membership)
                .changeType(ChangeType.UPGRADED)
                .fromTier(fromTier)
                .toTier(toTier)
                .changedAt(LocalDateTime.now())
                .build();
    }

    public static MembershipChangeHistory forDowngrade(UserMembership membership,
                                                       MembershipTier fromTier,
                                                       MembershipTier toTier) {
        return MembershipChangeHistory.builder()
                .userMembership(membership)
                .changeType(ChangeType.DOWNGRADED)
                .fromTier(fromTier)
                .toTier(toTier)
                .changedAt(LocalDateTime.now())
                .build();
    }

    public static MembershipChangeHistory forCancellation(UserMembership membership) {
        return MembershipChangeHistory.builder()
                .userMembership(membership)
                .changeType(ChangeType.CANCELLED)
                .fromTier(membership.getMembershipTier())
                .reason(membership.getCancellationReason())
                .changedAt(LocalDateTime.now())
                .build();
    }

    public static MembershipChangeHistory forRenewal(UserMembership membership) {
        return MembershipChangeHistory.builder()
                .userMembership(membership)
                .changeType(ChangeType.RENEWED)
                .toTier(membership.getMembershipTier())
                .changedAt(LocalDateTime.now())
                .build();
    }

    public static MembershipChangeHistory forExpiry(UserMembership membership) {
        return MembershipChangeHistory.builder()
                .userMembership(membership)
                .changeType(ChangeType.EXPIRED)
                .fromTier(membership.getMembershipTier())
                .changedAt(LocalDateTime.now())
                .build();
    }
}
