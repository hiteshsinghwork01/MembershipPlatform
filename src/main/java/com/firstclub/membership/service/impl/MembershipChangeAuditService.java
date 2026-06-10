package com.firstclub.membership.service.impl;

import com.firstclub.membership.entity.MembershipChangeHistory;
import com.firstclub.membership.repository.MembershipChangeHistoryRepository;
import com.firstclub.membership.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipChangeAuditService implements AuditService<MembershipChangeHistory> {

    private final MembershipChangeHistoryRepository changeHistoryRepository;

    @Override
    @Transactional
    public void record(MembershipChangeHistory entry) {
        try {
            changeHistoryRepository.save(entry);
            log.debug("Audit recorded: [{}] for membership [{}]",
                    entry.getChangeType(), entry.getUserMembership().getId());
        } catch (Exception e) {
            log.error("Audit write failed [{}] for membership [{}]: {}",
                    entry.getChangeType(), entry.getUserMembership().getId(), e.getMessage());
        }
    }
}
