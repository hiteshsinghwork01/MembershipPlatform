package com.firstclub.membership.dto;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TierTransitionResult {
    private User user;
    private UserMembership membership;
    private MembershipTier fromTier;
    private MembershipTier toTier;
    private boolean tierChanged;
}
