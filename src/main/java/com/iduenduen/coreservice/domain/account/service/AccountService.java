package com.iduenduen.coreservice.domain.account.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.dto.AccountBalanceResponse;
import com.iduenduen.coreservice.domain.account.dto.AccountInfoResponse;
import com.iduenduen.coreservice.domain.account.entity.Account;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.account.dto.EtfTradeNotificationRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.service.ExecutionGoalLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEtfHistoryRepository accountEtfHistoryRepository;
    private final ExecutionGoalLinkService executionGoalLinkService;

    public AccountInfoResponse getMyAccount(Long parentId) {
        Account account = accountRepository.findByParentId(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));
        return AccountInfoResponse.from(account);
    }

    public AccountBalanceResponse getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        return AccountBalanceResponse.builder()
                .availableCash(account.getAvailableAmt())
                .d0Balance(account.getAvailableAmt())
                .d1Balance(account.getAvailableAmt())
                .d2Balance(account.getAvailableAmt())
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
}
