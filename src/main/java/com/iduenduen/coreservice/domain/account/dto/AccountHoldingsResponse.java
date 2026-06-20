package com.iduenduen.coreservice.domain.account.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class AccountHoldingsResponse {
    private Long availableAmt;
    private List<HoldingDto> holdings;

    @Getter
    @Builder
    public static class HoldingDto {
        private Long etfId;
        private int qty;
        private Long avgBuyPrice;
    }
}