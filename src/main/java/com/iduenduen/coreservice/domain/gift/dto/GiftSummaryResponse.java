package com.iduenduen.coreservice.domain.gift.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GiftSummaryResponse(
        @JsonProperty("total_gifted_amt") long totalGiftedAmt,
        @JsonProperty("yearly") List<YearlyAmount> yearly
) {
    public record YearlyAmount(
            int year,
            long amount
    ) {}
}