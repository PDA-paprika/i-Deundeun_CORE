package com.iduenduen.coreservice.domain.account.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.dto.*;
import com.iduenduen.coreservice.domain.account.entity.Account;
import com.iduenduen.coreservice.domain.account.entity.AccountCashHistory;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHolding;
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
        int qtyDelta = req.eventType() == EtfEventType.SELL ? -req.qty() : req.qty();

        AccountEtfHistory history = AccountEtfHistory.builder()
            .accountId(req.accountId())
            .eventType(req.eventType())
            .externalEtfId(req.externalEtfId())
            .qtyDelta(qtyDelta)
            .price(req.price())
            .referenceId(req.referenceId())
            .referenceType(req.referenceType())
            .memo(req.memo())
            .occurredAt(req.occurredAt())
            .build();
        accountEtfHistoryRepository.save(history);

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
