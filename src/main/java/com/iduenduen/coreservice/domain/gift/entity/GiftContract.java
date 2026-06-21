package com.iduenduen.coreservice.domain.gift.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iduenduen.coreservice.common.base.BaseEntity;
import com.iduenduen.coreservice.domain.gift.enums.ContractStatus;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;

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
@Table(name = "gift_contracts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftContract extends BaseEntity {

	public static final BigDecimal DEFAULT_DISCOUNT_RATE = BigDecimal.valueOf(0.03);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "parent_id", nullable = false)
	private Long parentId;

	@Column(name = "child_id", nullable = false)
	private Long childId;

	@Column(name = "from_account_id", nullable = false)
	private Long fromAccountId;

	@Column(name = "to_account_id", nullable = false)
	private Long toAccountId;

	@Column(name = "external_etf_id")
	private Long externalEtfId;

	@Enumerated(EnumType.STRING)
	@Column(name = "gift_type", nullable = false, length = 20)
	private GiftType giftType;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private ContractStatus status = ContractStatus.DRAFT;

	@Column(name = "title", length = 200)
	private String title;

	@Column(name = "qty")
	private Integer qty;

	@Column(name = "cash_amount", nullable = false)
	private Long cashAmount = 0L;

	@Column(name = "transfer_day")
	private Integer transferDay;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "tax_free_limit_amt", nullable = false)
	private Long taxFreeLimitAmt = 0L;

	@Column(name = "discount_rate", nullable = false, precision = 5, scale = 4)
	private BigDecimal discountRate = DEFAULT_DISCOUNT_RATE;

	@Column(name = "cancelled_at")
	private LocalDateTime cancelledAt;

	@Builder
	public GiftContract(Long parentId, Long childId, Long fromAccountId, Long toAccountId,
		Long externalEtfId, GiftType giftType,
		String title, Integer qty, Long cashAmount, Integer transferDay,
		LocalDate startDate, LocalDate endDate, Long taxFreeLimitAmt, BigDecimal discountRate) {
		this.parentId = parentId;
		this.childId = childId;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.externalEtfId = externalEtfId;
		this.giftType = giftType;
		this.status = ContractStatus.DRAFT;
		this.title = title;
		this.qty = qty;
		this.cashAmount = cashAmount != null ? cashAmount : 0L;
		this.transferDay = transferDay;
		this.startDate = startDate;
		this.endDate = endDate;
		this.taxFreeLimitAmt = taxFreeLimitAmt != null ? taxFreeLimitAmt : 0L;
		this.discountRate = discountRate != null ? discountRate : DEFAULT_DISCOUNT_RATE;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void activate() {
		this.status = ContractStatus.ACTIVE;
	}

	public void cancel() {
		this.status = ContractStatus.CANCELLED;
		this.cancelledAt = LocalDateTime.now();
	}

	public void complete() {
		this.status = ContractStatus.COMPLETED;
	}

	public void fail() {
		this.status = ContractStatus.FAILED;
	}
}