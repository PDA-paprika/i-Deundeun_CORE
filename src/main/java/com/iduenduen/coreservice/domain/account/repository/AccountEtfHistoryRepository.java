package com.iduenduen.coreservice.domain.account.repository;

import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountEtfHistoryRepository extends JpaRepository<AccountEtfHistory, Long> {
    List<AccountEtfHistory> findByAccountIdOrderByOccurredAtDesc(Long accountId);

    @Query(value = "SELECT id, logo_img FROM etfs WHERE id IN :etfIds", nativeQuery = true)
    List<Object[]> findLogoImgByEtfIds(@Param("etfIds") List<Long> etfIds);
    Optional<AccountEtfHistory> findFirstByReferenceIdAndEventType(String referenceId, EtfEventType eventType);
}
