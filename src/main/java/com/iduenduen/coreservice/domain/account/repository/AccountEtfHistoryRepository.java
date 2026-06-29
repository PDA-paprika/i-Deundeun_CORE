package com.iduenduen.coreservice.domain.account.repository;

import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountEtfHistoryRepository extends JpaRepository<AccountEtfHistory, Long> {
    List<AccountEtfHistory> findByAccountIdOrderByOccurredAtDesc(Long accountId);

    @Query(value = "SELECT id, logo_img FROM etfs WHERE id IN :etfIds", nativeQuery = true)
    List<Object[]> findLogoImgByEtfIds(@Param("etfIds") List<Long> etfIds);
}
