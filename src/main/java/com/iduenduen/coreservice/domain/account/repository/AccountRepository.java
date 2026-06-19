package com.iduenduen.coreservice.domain.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountIdAndParentId(Long accountId, Long parentId);
    Optional<Account> findByAccountIdAndChildId(Long accountId, Long childId);
    Optional<Account> findByAccountId(Long accountId);
    Optional<Account> findByChildId(Long childId);
    Optional<Account> findByParentId(Long parentId);
}
