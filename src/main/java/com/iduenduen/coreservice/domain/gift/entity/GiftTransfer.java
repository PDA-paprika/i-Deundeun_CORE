package com.iduenduen.coreservice.domain.gift.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iduenduen.coreservice.common.base.BaseEntity;
import com.iduenduen.coreservice.domain.gift.enums.CheckStatus;
import com.iduenduen.coreservice.domain.gift.enums.TransferStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gift_transfers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftTransfer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "gift_contract_id", nullable = false)
	private Long giftContractId;

	@Column(name = "sequence_no", nullable = false)
	private int sequenceNo = 1;

	@Column(name = "scheduled_date", nullable = false)
	private LocalDate scheduledDate;

	@Column(name = "required_cash_amt", nullable = false)
	private long requiredCashAmt = 0L;

	@Column(name = "available_cash_amt", nullable = false)
	private long availableCashAmt = 0L;

	@Column(name = "required_etf_qty", nullable = false)
	private int requiredEtfQty = 0;

	@Column(name = "available_etf_qty", nullable = false)
	private int availableEtfQty = 0;

	@Column(name = "retry_count", nullable = false)
	private int retryCount = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "check_status", nullable = false, length = 20)
	private CheckStatus checkStatus = CheckStatus.UNCHECKED;

	@Column(name = "failure_reason", length = 500)
	private String failureReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private TransferStatus status = TransferStatus.SCHEDULED;

	@Column(name = "transferred_cash_amt", nullable = false)
	private long transferredCashAmt = 0L;

	@Column(name = "transferred_etf_qty", nullable = false)
	private int transferredEtfQty = 0;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Builder
	public GiftTransfer(Long giftContractId, int sequenceNo, LocalDate scheduledDate,
		long requiredCashAmt, long availableCashAmt, int requiredEtfQty, int availableEtfQty) {
		this.giftContractId = giftContractId;
		this.sequenceNo = sequenceNo;
		this.scheduledDate = scheduledDate;
		this.requiredCashAmt = requiredCashAmt;
		this.availableCashAmt = availableCashAmt;
		this.requiredEtfQty = requiredEtfQty;
		this.availableEtfQty = availableEtfQty;
	}

	public void recordAvailableAssets(long availableCashAmt, int availableEtfQty) {
		this.availableCashAmt = availableCashAmt;
		this.availableEtfQty = availableEtfQty;
	}

	public void incrementRetry() {
		this.retryCount++;
	}

	public void complete(long transferredCashAmt, int transferredEtfQty) {
		this.status = TransferStatus.COMPLETED;
		this.transferredCashAmt = transferredCashAmt;
		this.transferredEtfQty = transferredEtfQty;
		this.completedAt = LocalDateTime.now();
	}

	public void fail(String reason) {
		this.status = TransferStatus.FAILED;
		this.checkStatus = CheckStatus.FAILED;
		this.failureReason = reason;
	}

	public void cancel() {
		this.status = TransferStatus.CANCELLED;
	}

	public void skip() {
		this.status = TransferStatus.SKIPPED;
	}
}