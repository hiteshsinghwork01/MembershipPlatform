package com.firstclub.membership.util;

import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.entity.MembershipTier;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MembershipTierUtil {

    public static MembershipTier buildMembershipTier(CreateTierRequest request) {
        return MembershipTier.builder()
                .name(request.getName())
                .description(request.getDescription())
                .tierLevel(request.getTierLevel())
                .criteriaLogic(request.getCriteriaLogic())
                .active(true)
                .build();
    }

    public static void updateMembershipTier(UpdateTierRequest request, MembershipTier tier) {
        if (request.getName() != null) {
            tier.setName(request.getName());
        }
        if (request.getDescription() != null) {
            tier.setDescription(request.getDescription());
        }
        if (request.getTierLevel() != null) {
            tier.setTierLevel(request.getTierLevel());
        }
        if (request.getCriteriaLogic() != null) {
            tier.setCriteriaLogic(request.getCriteriaLogic());
        }
    }
}
