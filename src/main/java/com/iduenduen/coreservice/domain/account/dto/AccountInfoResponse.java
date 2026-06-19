package com.iduenduen.coreservice.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.account.entity.Account;

public record AccountInfoResponse(
        @JsonProperty("account_id")
        Long accountId,

        @JsonProperty("account_number")
        String accountNumber,

        @JsonProperty("available_amt")
        Long availableAmt
) {
    public static AccountInfoResponse from(Account account) {
        return new AccountInfoResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                account.getAvailableAmt()
        );
    }
}