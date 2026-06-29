package com.iduenduen.coreservice.domain.account.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.dto.*;
import com.iduenduen.coreservice.domain.account.entity.*;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.enums.ReferenceType;
import com.iduenduen.coreservice.domain.account.repository.AccountCashHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.service.ExecutionGoalLinkService;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;
import com.iduenduen.coreservice.domain.gift.repository.GiftContractRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEtfHistoryRepository accountEtfHistoryRepository;
    private final ExecutionGoalLinkService executionGoalLinkService;
    private final AccountEtfHoldingRepository accountEtfHoldingRepository;
    private final AccountCashHistoryRepository accountCashHistoryRepository;
    private final ChildrenRepository childrenRepository;
    private final GiftContractRepository giftContractRepository;
    private final GiftTransferRepository giftTransferRepository;

    public AccountInfoResponse getMyAccount(Long parentId) {
        Account account = accountRepository.findByParentId(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
        return AccountInfoResponse.from(account);
    }

    public AccountBalanceResponse getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        return AccountBalanceResponse.builder()
                .availableAmt(account.getAvailableAmt())
                .build();
    }

    @Transactional
    public Long recordTrade(EtfTradeNotificationRequest req) {
        Account account = accountRepository.findById(req.accountId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        long tradeAmount = (long) req.qty() * req.price();
        int qtyDelta = req.eventType() == EtfEventType.SELL ? -req.qty() : req.qty();

        // 예수금 업데이트
        if (req.eventType() == EtfEventType.BUY) {
            account.deductCash(tradeAmount);
        } else if (req.eventType() == EtfEventType.SELL) {
            account.addCash(tradeAmount);
        }

        // 보유 ETF upsert
        AccountEtfHoldingId holdingId = new AccountEtfHoldingId(req.etfId(), req.accountId());
        AccountEtfHolding holding = accountEtfHoldingRepository.findById(holdingId)
                .orElse(null);

        if (req.eventType() == EtfEventType.BUY) {
            if (holding == null) {
                holding = AccountEtfHolding.builder()
                        .id(holdingId)
                        .qty(req.qty())
                        .avgBuyPrice(req.price())
                        .build();
            } else {
                long totalCost = holding.getAvgBuyPrice() * holding.getQty() + tradeAmount;
                int newQty = holding.getQty() + req.qty();
                holding.update(newQty, totalCost / newQty);
            }
            accountEtfHoldingRepository.save(holding);
        } else if (req.eventType() == EtfEventType.SELL) {
            if (holding != null) {
                int newQty = holding.getQty() - req.qty();
                if (newQty <= 0) {
                    accountEtfHoldingRepository.delete(holding);
                } else {
                    holding.update(newQty, holding.getAvgBuyPrice());
                    accountEtfHoldingRepository.save(holding);
                }
            }
        }

        // 히스토리 저장
        AccountEtfHistory history = AccountEtfHistory.builder()
                .accountId(req.accountId())
                .eventType(req.eventType())
                .etfId(req.etfId())
                .etfNameSnapshot(req.etfName())
                .qtyDelta(qtyDelta)
                .price(req.price())
                .referenceId(req.referenceId())
                .referenceType(req.referenceType())
                .memo(req.memo())
                .occurredAt(req.occurredAt())
                .build();
        accountEtfHistoryRepository.save(history);

        if (req.eventType() == EtfEventType.SELL) {
            if (req.linkId() != null) {
                executionGoalLinkService.deductByLinkId(req.linkId(), req.qty());
            } else {
                executionGoalLinkService.deductByFifo(req.childId(), req.goalId(), req.etfId(), req.qty());
            }
            return null;
        }

        return executionGoalLinkService.createLink(
                req.parentId(), history.getId(), req.childId(), req.goalId(), req.memo());
    }

    public AccountHoldingsResponse getHoldings(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        List<AccountEtfHolding> holdings = accountEtfHoldingRepository.findByIdAccountId(accountId);

        List<AccountHoldingsResponse.HoldingDto> holdingDtos = holdings.stream()
                .map(h -> AccountHoldingsResponse.HoldingDto.builder()
                        .etfId(h.getId().getEtfId())
                        .qty(h.getQty())
                        .avgBuyPrice(h.getAvgBuyPrice())
                        .build())
                .toList();

        return AccountHoldingsResponse.builder()
                .availableAmt(account.getAvailableAmt())
                .holdings(holdingDtos)
                .build();
    }

    public AccountHoldingsResponse getUnallocatedHoldings(Long parentId) {
        Account account = accountRepository.findByParentId(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        List<AccountEtfHolding> holdings = accountEtfHoldingRepository.findByIdAccountId(account.getAccountId());

        Map<Long, Integer> taggedQtyMap = executionGoalLinkService.getTaggedQtyMapByEtfId(parentId);

        List<AccountHoldingsResponse.HoldingDto> holdingDtos = holdings.stream()
                .map(h -> {
                    int tagged = taggedQtyMap.getOrDefault(h.getId().getEtfId(), 0);
                    int unallocated = h.getQty() - tagged;
                    return AccountHoldingsResponse.HoldingDto.builder()
                            .etfId(h.getId().getEtfId())
                            .qty(unallocated)
                            .avgBuyPrice(h.getAvgBuyPrice())
                            .build();
                })
                .filter(dto -> dto.getQty() > 0)
                .toList();

        return AccountHoldingsResponse.builder()
                .availableAmt(account.getAvailableAmt())
                .holdings(holdingDtos)
                .build();
    }

    public AccountCashHistoriesResponse getCashHistories(Long accountId) {
        List<AccountCashHistory> histories = accountCashHistoryRepository
                .findByAccountIdOrderByOccurredAtDesc(accountId);

        List<AccountCashHistoriesResponse.HistoryDto> dtos = histories.stream()
                .map(h -> AccountCashHistoriesResponse.HistoryDto.builder()
                        .id(h.getId())
                        .eventType(h.getEventType().name())
                        .amountDelta(h.getAmountDelta())
                        .balanceAfter(h.getBalanceAfter())
                        .memo(h.getMemo())
                        .occurredAt(h.getOccurredAt())
                        .build())
                .toList();

        return AccountCashHistoriesResponse.builder()
                .totalCount(dtos.size())
                .histories(dtos)
                .build();
    }

    @Transactional
    public EtfGiftResponse transferEtf(Long parentId, EtfGiftRequest req) {
        childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(req.childId(), parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

        Account parentAccount = accountRepository.findByParentId(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
        Account childAccount = accountRepository.findByChildId(req.childId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        AccountEtfHoldingId holdingId = new AccountEtfHoldingId(req.etfId(), parentAccount.getAccountId());
        AccountEtfHolding parentHolding = accountEtfHoldingRepository.findById(holdingId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INSUFFICIENT_ETF_QTY));

        executionGoalLinkService.deductUnallocatedForGift(parentId, req.etfId(), req.qty());

        int newParentQty = parentHolding.getQty() - req.qty();
        if (newParentQty == 0) {
            accountEtfHoldingRepository.delete(parentHolding);
        } else {
            parentHolding.update(newParentQty, parentHolding.getAvgBuyPrice());
            accountEtfHoldingRepository.save(parentHolding);
        }

        long currentPrice = req.currentPrice();

        AccountEtfHoldingId childHoldingId = new AccountEtfHoldingId(req.etfId(), childAccount.getAccountId());
        AccountEtfHolding childHolding = accountEtfHoldingRepository.findById(childHoldingId).orElse(null);
        if (childHolding == null) {
            childHolding = AccountEtfHolding.builder()
                    .id(childHoldingId)
                    .qty(req.qty())
                    .avgBuyPrice(currentPrice)
                    .build();
        } else {
            long totalCost = childHolding.getAvgBuyPrice() * childHolding.getQty() + currentPrice * req.qty();
            int newChildQty = childHolding.getQty() + req.qty();
            childHolding.update(newChildQty, totalCost / newChildQty);
        }
        accountEtfHoldingRepository.save(childHolding);

        LocalDate today = LocalDate.now();
        long giftAmount = currentPrice * req.qty();
        String etfName = req.etfName() != null ? req.etfName() : "";
        String title = req.memo() != null && !req.memo().isBlank() ? req.memo() : etfName + " 증여";

        GiftContract contract = GiftContract.builder()
                .parentId(parentId)
                .childId(req.childId())
                .fromAccountId(parentAccount.getAccountId())
                .toAccountId(childAccount.getAccountId())
                .externalEtfId(req.etfId())
                .giftType(GiftType.ETF)
                .title(title)
                .qty(req.qty())
                .cashAmount(0L)
                .startDate(today)
                .endDate(today)
                .build();
        contract.initEtfValuation(today, giftAmount);
        contract.confirmFinalGiftAmount(giftAmount);
        contract.activate();
        contract.complete();
        giftContractRepository.save(contract);

        GiftTransfer transfer = GiftTransfer.builder()
                .giftContractId(contract.getId())
                .sequenceNo(1)
                .scheduledDate(today)
                .requiredCashAmt(0L)
                .availableCashAmt(0L)
                .requiredEtfQty(req.qty())
                .availableEtfQty(req.qty())
                .build();
        transfer.complete(0L, req.qty());
        giftTransferRepository.save(transfer);

        String contractIdStr = contract.getId().toString();
        LocalDateTime now = LocalDateTime.now();

        accountEtfHistoryRepository.save(AccountEtfHistory.builder()
                .accountId(parentAccount.getAccountId())
                .eventType(EtfEventType.GIFT_ETF_OUT)
                .etfId(req.etfId())
                .etfNameSnapshot(etfName)
                .qtyDelta(-req.qty())
                .price(currentPrice)
                .referenceId(contractIdStr)
                .referenceType(ReferenceType.GIFT)
                .memo(req.memo())
                .occurredAt(now)
                .build());

        accountEtfHistoryRepository.save(AccountEtfHistory.builder()
                .accountId(childAccount.getAccountId())
                .eventType(EtfEventType.GIFT_ETF_IN)
                .etfId(req.etfId())
                .etfNameSnapshot(etfName)
                .qtyDelta(req.qty())
                .price(currentPrice)
                .referenceId(contractIdStr)
                .referenceType(ReferenceType.GIFT)
                .memo(req.memo())
                .occurredAt(now)
                .build());

        return new EtfGiftResponse(contract.getId());
    }

    public AccountEtfHistoriesResponse getEtfHistories(Long accountId) {
        List<AccountEtfHistory> histories = accountEtfHistoryRepository
                .findByAccountIdOrderByOccurredAtDesc(accountId);

        List<AccountEtfHistoriesResponse.HistoryDto> dtos = histories.stream()
                .map(h -> AccountEtfHistoriesResponse.HistoryDto.builder()
                        .id(h.getId())
                        .eventType(h.getEventType().name())
                        .etfNameSnapshot(h.getEtfNameSnapshot())
                        .qtyDelta(h.getQtyDelta())
                        .price(h.getPrice())
                        .occurredAt(h.getOccurredAt())
                        .build())
                .toList();

        return AccountEtfHistoriesResponse.builder()
                .totalCount(dtos.size())
                .histories(dtos)
                .build();
    }
}
