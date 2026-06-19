package com.iduenduen.coreservice.domain.gift.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.entity.Account;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHoldingId;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateRequest;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractDetailResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractListResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftPreviewResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftSummaryResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftTransferHistoryResponse;
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
	private final AccountEtfHoldingRepository accountEtfHoldingRepository;

	// 증여 계약 목록 조회
	public GiftContractListResponse getGiftContracts(Long parentId, Long childId) {
		childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		List<GiftContract> contracts = giftContractRepository.findAllByChildIdAndCancelledAtIsNull(childId);
		List<Long> contractIds = contracts.stream().map(GiftContract::getId).toList();
		List<GiftTransfer> allTransfers = giftTransferRepository.findAllByGiftContractIdIn(contractIds);

		List<GiftContractListResponse.GiftContractItem> items = contracts.stream()
			.map(contract -> {
				int transferCount = (int) allTransfers.stream()
					.filter(t -> t.getGiftContractId().equals(contract.getId()))
					.count();
				return GiftContractListResponse.GiftContractItem.from(contract, transferCount);
			})
			.toList();

		return new GiftContractListResponse(items);
	}

	// 자녀별 증여 현황 요약 (총 증여액 + 연도별)
	public GiftSummaryResponse getGiftSummary(Long parentId, Long childId) {
		childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		List<GiftContract> contracts = giftContractRepository.findAllByChildId(childId);
		List<Long> contractIds = contracts.stream().map(GiftContract::getId).toList();
		List<GiftTransfer> completedTransfers = giftTransferRepository.findAllByGiftContractIdIn(contractIds)
			.stream()
			.filter(t -> t.getStatus() == com.iduenduen.coreservice.domain.gift.enums.TransferStatus.COMPLETED)
			.toList();

		long totalGiftedAmt = completedTransfers.stream()
			.mapToLong(GiftTransfer::getTransferredCashAmt)
			.sum();

		List<GiftSummaryResponse.YearlyAmount> yearly = completedTransfers.stream()
			.collect(Collectors.groupingBy(
				t -> t.getCompletedAt().getYear(),
				Collectors.summingLong(GiftTransfer::getTransferredCashAmt)
			))
			.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(e -> new GiftSummaryResponse.YearlyAmount(e.getKey(), e.getValue()))
			.toList();

		return new GiftSummaryResponse(totalGiftedAmt, yearly);
	}

	// 자녀별 이체 내역 조회
	public GiftTransferHistoryResponse getTransfersByChild(Long parentId, Long childId) {
		childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		List<GiftContract> contracts = giftContractRepository.findAllByChildId(childId);
		List<Long> contractIds = contracts.stream().map(GiftContract::getId).toList();
		List<GiftTransfer> transfers = giftTransferRepository.findAllByGiftContractIdIn(contractIds);

		Map<Long, GiftContract> contractMap = contracts.stream()
			.collect(java.util.stream.Collectors.toMap(GiftContract::getId, c -> c));

		List<GiftTransferHistoryResponse.TransferHistoryItem> items = transfers.stream()
			.map(t -> GiftTransferHistoryResponse.TransferHistoryItem.of(t, contractMap.get(t.getGiftContractId())))
			.toList();

		return new GiftTransferHistoryResponse(items);
	}

	// 부모 전체 이체 내역 조회
	public GiftTransferHistoryResponse getTransfersByParent(Long parentId) {
		List<GiftContract> contracts = giftContractRepository.findAllByParentId(parentId);
		List<Long> contractIds = contracts.stream().map(GiftContract::getId).toList();
		List<GiftTransfer> transfers = giftTransferRepository.findAllByGiftContractIdIn(contractIds);

		Map<Long, GiftContract> contractMap = contracts.stream()
			.collect(Collectors.toMap(GiftContract::getId, c -> c));

		List<GiftTransferHistoryResponse.TransferHistoryItem> items = transfers.stream()
			.map(t -> GiftTransferHistoryResponse.TransferHistoryItem.of(t, contractMap.get(t.getGiftContractId())))
			.toList();

		return new GiftTransferHistoryResponse(items);
	}

	// 증여 계약 취소
	@Transactional
	public void cancelGiftContract(Long parentId, Long contractId) {
		GiftContract contract = giftContractRepository.findByIdAndParentId(contractId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.GIFT_CONTRACT_NOT_FOUND));

		if (contract.getStatus() != com.iduenduen.coreservice.domain.gift.enums.ContractStatus.DRAFT
			&& contract.getStatus() != com.iduenduen.coreservice.domain.gift.enums.ContractStatus.ACTIVE) {
			throw new GeneralException(ErrorStatus.GIFT_CONTRACT_NOT_CANCELLABLE);
		}

		List<GiftTransfer> transfers = giftTransferRepository.findAllByGiftContractIdOrderBySequenceNoAsc(contractId);
		transfers.stream()
			.filter(t -> t.getStatus() == com.iduenduen.coreservice.domain.gift.enums.TransferStatus.SCHEDULED)
			.forEach(GiftTransfer::cancel);

		contract.cancel();
	}

	// 증여 계약 상세 조회
	public GiftContractDetailResponse getGiftContractDetail(Long parentId, Long contractId) {
		GiftContract contract = giftContractRepository.findByIdAndParentId(contractId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.GIFT_CONTRACT_NOT_FOUND));
		List<GiftTransfer> transfers = giftTransferRepository.findAllByGiftContractIdOrderBySequenceNoAsc(contractId);
		return GiftContractDetailResponse.of(contract, transfers);
	}

	// 증여 예상 정보 계산
	public GiftPreviewResponse preview(Long parentId, GiftContractCreateRequest request) {
		validateRequest(request);
		Account fromAccount = accountRepository.findByParentId(parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
		validateAssets(fromAccount, request);
		return calculatePreview(request);
	}

	// preview와 등록에서 공통으로 사용하는 계산 로직
	private GiftPreviewResponse calculatePreview(GiftContractCreateRequest request) {
		LocalDate firstDate = resolveStartDate(request);
		LocalDate lastDate = resolveEndDate(request);

		return switch (request.giftType()) {
			case INSTALLMENT -> {
				int transferCount = (int) YearMonth.from(firstDate)
					.until(YearMonth.from(lastDate), ChronoUnit.MONTHS) + 1;
				yield new GiftPreviewResponse(
					request.giftType(),
					request.cashAmount(),
					null,
					request.cashAmount() * transferCount,
					transferCount,
					firstDate,
					lastDate
				);
			}
			case ONE_TIME -> new GiftPreviewResponse(
				request.giftType(),
				request.cashAmount(),
				null,
				request.cashAmount(),
				1,
				firstDate,
				null
			);
			case ETF -> new GiftPreviewResponse(
				request.giftType(),
				null,
				request.qty(),
				0L,
				1,
				firstDate,
				null
			);
		};
	}

	// 증여 계약 등록
	@Transactional
	public GiftContractCreateResponse registerGiftContract(Long parentId, Long childId,
		GiftContractCreateRequest request) {
		childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		Account fromAccount = accountRepository.findByParentId(parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
		Long toAccountId = accountRepository.findByChildId(childId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND))
			.getAccountId();
		validateAccounts(fromAccount.getAccountId(), toAccountId, parentId, childId);
		validateRequest(request);
		validateAssets(fromAccount, request);

		GiftPreviewResponse preview = calculatePreview(request);

		GiftContract contract = GiftContract.builder()
			.parentId(parentId)
			.childId(childId)
			.fromAccountId(fromAccount.getAccountId())
			.toAccountId(toAccountId)
			.giftType(request.giftType())
			.title(request.title())
			.cashAmount(request.cashAmount() != null ? request.cashAmount() : 0L)
			.transferDay(request.transferDay())
			.startDate(preview.firstTransferDate())
			.endDate(preview.lastTransferDate())
			.externalEtfId(request.externalEtfId())
			.qty(request.qty())
			.build();

		giftContractRepository.save(contract);

		List<GiftTransfer> transfers = createTransfers(contract);
		giftTransferRepository.saveAll(transfers);
		executeImmediateTransfer(fromAccount, contract, transfers, request);

		int transferCount = preview.transferCount();
		long expectedTotalAmount = preview.expectedTotalAmount();

		return new GiftContractCreateResponse(
			contract.getId(),
			contract.getGiftType(),
			contract.getStatus(),
			GiftContractCreateResponse.messageFrom(contract.getStatus()),
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

	// ONE_TIME, ETF는 당일 즉시 차감 및 상태 변경
	private void executeImmediateTransfer(Account fromAccount, GiftContract contract,
		List<GiftTransfer> transfers, GiftContractCreateRequest request) {
		switch (contract.getGiftType()) {
			case ONE_TIME -> {
				fromAccount.deductCash(contract.getCashAmount());
				transfers.get(0).complete(contract.getCashAmount(), 0);
				contract.complete();
			}
			case ETF -> {
				AccountEtfHoldingId holdingId = new AccountEtfHoldingId(
					String.valueOf(request.externalEtfId()), fromAccount.getAccountId());
				accountEtfHoldingRepository.findById(holdingId)
					.ifPresent(holding -> holding.deductQty(contract.getQty()));
				transfers.get(0).complete(0, contract.getQty());
				contract.complete();
			}
			case INSTALLMENT -> contract.activate();
		}
	}

	// 현금 잔액 및 ETF 수량 검증
	private void validateAssets(Account fromAccount, GiftContractCreateRequest request) {
		switch (request.giftType()) {
			case INSTALLMENT, ONE_TIME -> {
				if (fromAccount.getAvailableAmt() < request.cashAmount()) {
					throw new GeneralException(ErrorStatus.INSUFFICIENT_BALANCE);
				}
			}
			case ETF -> {
				AccountEtfHoldingId holdingId = new AccountEtfHoldingId(
					String.valueOf(request.externalEtfId()), fromAccount.getAccountId());
				int availableQty = accountEtfHoldingRepository.findById(holdingId)
					.map(h -> h.getQty())
					.orElse(0);
				if (availableQty < request.qty()) {
					throw new GeneralException(ErrorStatus.INSUFFICIENT_ETF_QTY);
				}
			}
		}
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
				if (!request.startDate().isEqual(LocalDate.now())) {
					throw new GeneralException(ErrorStatus.GIFT_CONTRACT_ONLY_TODAY);
				}
			}
			case ETF -> {
				if (request.externalEtfId() == null || request.qty() == null || request.startDate() == null) {
					throw new GeneralException(ErrorStatus.GIFT_CONTRACT_INVALID_FIELDS);
				}
				if (!request.startDate().isEqual(LocalDate.now())) {
					throw new GeneralException(ErrorStatus.GIFT_CONTRACT_ONLY_TODAY);
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
