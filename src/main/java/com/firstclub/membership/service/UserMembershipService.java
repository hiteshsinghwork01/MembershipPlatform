package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CancelMembershipRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.MembershipChangeHistoryResponse;
import com.firstclub.membership.dto.response.MembershipUpgradeResponse;
import com.firstclub.membership.dto.response.UserMembershipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserMembershipService {

    UserMembershipResponse subscribe(SubscribeRequest request);

    MembershipUpgradeResponse upgradeTier(Long membershipId);

    UserMembershipResponse downgradeTier(Long membershipId);

    UserMembershipResponse unsubscribe(Long membershipId, CancelMembershipRequest request);

    UserMembershipResponse getActiveMembership(Long userId);

    Page<MembershipChangeHistoryResponse> getChangeHistory(Long membershipId, Pageable pageable);
}
