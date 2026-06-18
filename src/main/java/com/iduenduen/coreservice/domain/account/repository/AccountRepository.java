package com.iduenduen.coreservice.domain.account.repository;

import com.iduenduen.coreservice.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
