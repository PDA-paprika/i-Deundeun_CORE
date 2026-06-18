package com.iduenduen.coreservice.domain.gift.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateRequest;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateResponse;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.repository.GiftContractRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftTransferRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GiftService {

	private final GiftContractRepository giftContractRepository;
	private final GiftTransferRepository giftTransferRepository;
	private final ChildrenRepository childrenRepository;
	private final AccountRepository accountRepository;

	// 증여 계약 등록
	@Transactional
	public GiftContractCreateResponse registerGiftContract(Long parentId, Long childId,
		GiftContractCreateRequest request) {
		childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		validateAccounts(request.fromAccountId(), request.toAccountId(), parentId, childId);
		validateRequest(request);

		LocalDate startDate = resolveStartDate(request);
		LocalDate endDate = resolveEndDate(request);

		GiftContract contract = GiftContract.builder()
			.parentId(parentId)
			.childId(childId)
			.fromAccountId(request.fromAccountId())
			.toAccountId(request.toAccountId())
			.giftType(request.giftType())
			.title(request.title())
			.cashAmount(request.cashAmount() != null ? request.cashAmount() : 0L)
			.transferDay(request.transferDay())
			.startDate(startDate)
			.endDate(endDate)
			.externalEtfId(request.externalEtfId())
			.qty(request.qty())
			.build();

		giftContractRepository.save(contract);

		List<GiftTransfer> transfers = createTransfers(contract);
		giftTransferRepository.saveAll(transfers);

		int transferCount = transfers.size();
		long expectedTotalAmount = calculateExpectedTotalAmount(contract, transferCount);

		return new GiftContractCreateResponse(
			contract.getId(),
			contract.getGiftType(),
			contract.getStatus(),
			expectedTotalAmount,
			transferCount
		);
	}

	// INSTALLMENT: 오늘 기준 다음 이체일 계산! 나머지: 프론트 전달값 그대로
	private LocalDate resolveStartDate(GiftContractCreateRequest request) {
		if (request.giftType() != com.iduenduen.coreservice.domain.gift.enums.GiftType.INSTALLMENT) {
			return request.startDate();
		}
		int transferDay = request.transferDay();
		LocalDate today = LocalDate.now();
		YearMonth thisMonth = YearMonth.now();
		LocalDate candidateThisMonth = thisMonth.atDay(Math.min(transferDay, thisMonth.lengthOfMonth()));
		if (candidateThisMonth.isAfter(today)) {
			return candidateThisMonth;
		}
		YearMonth nextMonth = thisMonth.plusMonths(1);
		return nextMonth.atDay(Math.min(transferDay, nextMonth.lengthOfMonth()));
	}

	// INSTALLMENT: 종료 연월의 transfer_day를 실제 종료일로 변환
	private LocalDate resolveEndDate(GiftContractCreateRequest request) {
		if (request.giftType() != com.iduenduen.coreservice.domain.gift.enums.GiftType.INSTALLMENT) {
			return null;
		}
		YearMonth endMonth = request.endMonth();
		return endMonth.atDay(Math.min(request.transferDay(), endMonth.lengthOfMonth()));
	}

	// fromAccountId → parentId 소유 확인, toAccountId → childId 소유 확인
	private void validateAccounts(Long fromAccountId, Long toAccountId, Long parentId, Long childId) {
		accountRepository.findByAccountIdAndParentId(fromAccountId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_ACCESS_DENIED));
		accountRepository.findByAccountIdAndChildId(toAccountId, childId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_ACCESS_DENIED));
	}

	// 증여 유형별 필수 조건
	private void validateRequest(GiftContractCreateRequest request) {
		switch (request.giftType()) {
			case INSTALLMENT -> {
				if (request.cashAmount() == null || request.transferDay() == null || request.endMonth() == null) {
					throw new GeneralException(ErrorStatus.GIFT_CONTRACT_INVALID_FIELDS);
				}
			}
			case ONE_TIME -> {
				if (request.cashAmount() == null || request.startDate() == null) {
					throw new GeneralException(ErrorStatus.GIFT_CONTRACT_INVALID_FIELDS);
				}
			}
			case ETF -> {
				if (request.externalEtfId() == null || request.qty() == null || request.startDate() == null) {
					throw new GeneralException(ErrorStatus.GIFT_CONTRACT_INVALID_FIELDS);
				}
			}
		}
	}

	// 증여 유형에 따라 이체 방법 다르게
	private List<GiftTransfer> createTransfers(GiftContract contract) {
		return switch (contract.getGiftType()) {
			case INSTALLMENT -> createInstallmentTransfers(contract);
			case ONE_TIME -> List.of(GiftTransfer.builder()
				.giftContractId(contract.getId())
				.sequenceNo(1)
				.scheduledDate(contract.getStartDate())
				.requiredCashAmt(contract.getCashAmount())
				.build());
			case ETF -> List.of(GiftTransfer.builder()
				.giftContractId(contract.getId())
				.sequenceNo(1)
				.scheduledDate(contract.getStartDate())
				.requiredEtfQty(contract.getQty())
				.build());
		};
	}

	// 적립식 전용: 시작월~종료월까지 매월 이체
	private List<GiftTransfer> createInstallmentTransfers(GiftContract contract) {
		YearMonth startMonth = YearMonth.from(contract.getStartDate());
		YearMonth endMonth = YearMonth.from(contract.getEndDate());
		int totalMonths = (int) startMonth.until(endMonth, ChronoUnit.MONTHS) + 1;

		return IntStream.rangeClosed(1, totalMonths)
			.mapToObj(seq -> {
				YearMonth month = startMonth.plusMonths(seq - 1);
				int day = Math.min(contract.getTransferDay(), month.lengthOfMonth());
				return GiftTransfer.builder()
					.giftContractId(contract.getId())
					.sequenceNo(seq)
					.scheduledDate(month.atDay(day))
					.requiredCashAmt(contract.getCashAmount())
					.build();
			})
			.toList();
	}

	// 예상 총 증여액 계산
	private long calculateExpectedTotalAmount(GiftContract contract, int transferCount) {
		return switch (contract.getGiftType()) {
			case INSTALLMENT -> contract.getCashAmount() * transferCount;
			case ONE_TIME -> contract.getCashAmount();
			case ETF -> 0L;
		};
	}
}
