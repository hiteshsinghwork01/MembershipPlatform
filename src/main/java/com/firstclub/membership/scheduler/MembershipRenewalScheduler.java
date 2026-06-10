package com.firstclub.membership.scheduler;

import com.firstclub.membership.entity.MembershipChangeHistory;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.AuditService;
import com.firstclub.membership.util.ChangeHistoryUtil;
import com.firstclub.membership.util.MembershipDateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipRenewalScheduler {

    private static final int BATCH_SIZE = 500;
    private static final int MAX_BATCHES = 1000;

    private final UserMembershipRepository membershipRepository;
    private final AuditService<MembershipChangeHistory> auditService;

    @Scheduled(cron = "0 0 2 * * *")
    public void renewExpiringMemberships() {
        LocalDateTime renewalCutoff = LocalDateTime.now().plusHours(24);
        PageRequest pageable = PageRequest.of(0, BATCH_SIZE);
        int totalRenewed = 0;
        int batchCount = 0;

        List<UserMembership> batch;
        do {
            batch = membershipRepository.findByStatusAndAutoRenewTrueAndExpiryDateBefore(
                    MembershipStatus.ACTIVE, renewalCutoff, pageable);
            batchCount++;

            for (UserMembership membership : batch) {
                try {
                    int durationMonths = membership.getMembershipPlan().getDurationType().getMonths();
                    membership.setExpiryDate(
                            MembershipDateUtils.calculateExpiryDate(membership.getExpiryDate(), durationMonths));
                    membership.setLastRenewedAt(LocalDateTime.now());
                    membershipRepository.save(membership);
                    auditService.record(ChangeHistoryUtil.forRenewal(membership));
                    totalRenewed++;
                    log.info("Renewed membership {} for user {}", membership.getId(), membership.getUser().getId());
                } catch (Exception e) {
                    log.error("Failed to renew membership {}: {}", membership.getId(), e.getMessage());
                }
            }
        } while (batch.size() == BATCH_SIZE && batchCount < MAX_BATCHES);

        log.info("Renewal job complete: {} membership(s) renewed in {} batch(es)", totalRenewed, batchCount);
    }
}
