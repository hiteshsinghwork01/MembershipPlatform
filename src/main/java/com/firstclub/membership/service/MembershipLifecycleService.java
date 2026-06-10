package com.firstclub.membership.service;

import com.firstclub.membership.dto.TierTransitionResult;
import com.firstclub.membership.dto.request.CancelMembershipRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.entity.UserMembership;

public interface MembershipLifecycleService {

    UserMembership createSubscription(SubscribeRequest request);

    TierTransitionResult evaluateAndApplyUpgrade(Long membershipId);

    TierTransitionResult applyDowngrade(Long membershipId);

    UserMembership cancel(Long membershipId, CancelMembershipRequest request);

    UserMembership expireIfNeeded(UserMembership membership);
}
