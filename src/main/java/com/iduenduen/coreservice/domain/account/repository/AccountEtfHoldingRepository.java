package com.iduenduen.coreservice.domain.account.repository;


import com.iduenduen.coreservice.domain.account.entity.AccountEtfHolding;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHoldingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountEtfHoldingRepository extends JpaRepository<AccountEtfHolding, AccountEtfHoldingId> {
    List<AccountEtfHolding> findByIdAccountId(Long accountId);

    @Modifying
    @Query("DELETE FROM AccountEtfHolding h WHERE h.qty <= 0")
    int deleteByQtyLessThanEqualZero();
}
