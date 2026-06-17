package com.iduenduen.coreservice.domain.onboarding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.onboarding.entity.UserAgreement;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
    List<UserAgreement> findAllByParentId(Long parentId);
}
