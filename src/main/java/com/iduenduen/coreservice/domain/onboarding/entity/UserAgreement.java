package com.iduenduen.coreservice.domain.onboarding.entity;

import java.time.LocalDateTime;

import com.iduenduen.coreservice.common.base.BaseEntity;
import com.iduenduen.coreservice.domain.parent.entity.Parent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_agreements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @Column(name = "agreement_type", length = 20, nullable = false)
    private String agreementType;

    @Column(nullable = false)
    private Boolean agreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Builder
    public UserAgreement(Parent parent, String agreementType, Boolean agreed) {
        this.parent = parent;
        this.agreementType = agreementType;
        this.agreed = agreed;
        this.agreedAt = agreed ? LocalDateTime.now() : null;
    }
}
