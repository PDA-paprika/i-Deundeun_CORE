package com.iduenduen.coreservice.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_etf_holdings")
@Getter
@NoArgsConstructor
public class AccountEtfHolding {

    @EmbeddedId
    private AccountEtfHoldingId id;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "avg_buy_price", nullable = false)
    private long avgBuyPrice;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void deductQty(int qty) {
        this.qty -= qty;
    }
}
