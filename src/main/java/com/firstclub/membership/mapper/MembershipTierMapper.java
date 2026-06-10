package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.TierBenefitConfigResponse;
import com.firstclub.membership.entity.Benefit;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefitConfig;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MembershipTierMapper {

    public static MembershipTierResponse toResponse(MembershipTier tier) {
        List<TierBenefitConfigResponse> benefits = tier.getBenefitConfigs().stream()
                .filter(TierBenefitConfig::isApplicable)
                .map(MembershipTierMapper::toBenefitConfigResponse)
                .toList();

        return MembershipTierResponse.builder()
                .id(tier.getId())
                .name(tier.getName())
                .description(tier.getDescription())
                .tierLevel(tier.getTierLevel())
                .benefits(benefits)
                .build();
    }

    public static TierBenefitConfigResponse toBenefitConfigResponse(TierBenefitConfig config) {
        return TierBenefitConfigResponse.builder()
                .id(config.getId())
                .benefit(toBenefitResponse(config.getBenefit()))
                .discountPercentage(config.getDiscountPercentage())
                .metadata(config.getMetadata())
                .validFrom(config.getValidFrom())
                .validUntil(config.getValidUntil())
                .build();
    }

    private static BenefitResponse toBenefitResponse(Benefit benefit) {
        return BenefitResponse.builder()
                .id(benefit.getId())
                .name(benefit.getName())
                .description(benefit.getDescription())
                .benefitType(benefit.getBenefitType())
                .build();
    }
}
