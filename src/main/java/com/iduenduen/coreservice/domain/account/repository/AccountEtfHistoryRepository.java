package com.iduenduen.coreservice.domain.account.repository;

import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountEtfHistoryRepository extends JpaRepository<AccountEtfHistory, Long> {
    List<AccountEtfHistory> findByAccountIdOrderByOccurredAtDesc(Long accountId);
    Optional<AccountEtfHistory> findFirstByReferenceIdAndEventType(String referenceId, EtfEventType eventType);
}
