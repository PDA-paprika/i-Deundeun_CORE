package com.iduenduen.coreservice.domain.account.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountBalanceResponse {
    private Long availableAmt;
}
