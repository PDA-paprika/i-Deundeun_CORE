package com.iduenduen.coreservice.domain.parent.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.parent.entity.Parent;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByIdAndDeletedAtIsNull(Long id);

    Optional<Parent> findByAccountNumberAndDeletedAtIsNull(String accountNumber);

    boolean existsByEmail(String email);

    boolean existsByAccountNumber(String accountNumber);
}
