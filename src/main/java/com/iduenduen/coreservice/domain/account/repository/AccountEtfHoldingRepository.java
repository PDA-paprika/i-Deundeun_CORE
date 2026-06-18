package com.iduenduen.coreservice.domain.account.repository;


import com.iduenduen.coreservice.domain.account.entity.AccountEtfHolding;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHoldingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountEtfHoldingRepository extends JpaRepository<AccountEtfHolding, AccountEtfHoldingId> {
    List<AccountEtfHolding> findByIdAccountId(Long accountId);
}
