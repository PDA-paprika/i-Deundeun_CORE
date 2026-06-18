package com.iduenduen.coreservice.domain.account.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.dto.AccountBalanceResponse;
import com.iduenduen.coreservice.domain.account.dto.AccountInfoResponse;
import com.iduenduen.coreservice.domain.account.entity.Account;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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
}
