package com.firstclub.membership.util;

import com.firstclub.membership.dto.request.AddTierBenefitRequest;
import com.firstclub.membership.dto.request.UpdateTierBenefitRequest;
import com.firstclub.membership.entity.Benefit;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefitConfig;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class TierBenefitUtil {

    public static TierBenefitConfig build(MembershipTier tier, Benefit benefit, AddTierBenefitRequest request) {
        return TierBenefitConfig.builder()
                .membershipTier(tier)
                .benefit(benefit)
                .discountPercentage(request.getDiscountPercentage())
                .metadata(request.getMetadata())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .applicable(true)
                .build();
    }

    public static void updateTierBenefit(UpdateTierBenefitRequest request, TierBenefitConfig config) {
        if (request.getDiscountPercentage() != null) {
            config.setDiscountPercentage(request.getDiscountPercentage());
        }
        if (StringUtils.isNotBlank(request.getMetadata())) {
            config.setMetadata(request.getMetadata());
        }
        if (request.getValidFrom() != null) {
            config.setValidFrom(request.getValidFrom());
        }
        if (request.getValidUntil() != null) {
            config.setValidUntil(request.getValidUntil());
        }
        if (request.getApplicable() != null) {
            config.setApplicable(request.getApplicable());
        }
    }
}
