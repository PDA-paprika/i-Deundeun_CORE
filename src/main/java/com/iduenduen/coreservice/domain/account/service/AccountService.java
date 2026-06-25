package com.iduenduen.coreservice.domain.account.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.dto.*;
import com.iduenduen.coreservice.domain.account.entity.*;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.repository.AccountCashHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.service.ExecutionGoalLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            executionGoalLinkService.deductByFifo(req.childId(), req.goalId(), req.qty());
            return null;
        }

        return executionGoalLinkService.createLink(
                req.parentId(), history.getId(), req.childId(), req.goalId(), req.qty(), req.memo());
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
