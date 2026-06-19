package com.iduenduen.coreservice.domain.account.entity;

import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.enums.ReferenceType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_etf_histories")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AccountEtfHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 20, nullable = false)
    private EtfEventType eventType;

    @Column(name = "external_etf_id", nullable = false)
    private String externalEtfId;

    @Column(name = "qty_delta", nullable = false)
    private int qtyDelta;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "reference_id")
    private String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 20)
    private ReferenceType referenceType;

    @Column(name = "memo", length = 500)
    private String memo;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public AccountEtfHistory(Long accountId, EtfEventType eventType, String externalEtfId,
                              int qtyDelta, long price, String referenceId,
                              ReferenceType referenceType, String memo, LocalDateTime occurredAt) {
        this.accountId = accountId;
        this.eventType = eventType;
        this.externalEtfId = externalEtfId;
        this.qtyDelta = qtyDelta;
        this.price = price;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.memo = memo;
        this.occurredAt = occurredAt;
    }
}