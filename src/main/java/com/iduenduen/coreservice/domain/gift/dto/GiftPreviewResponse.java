package com.iduenduen.coreservice.domain.gift.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;

public record GiftPreviewResponse(
        @JsonProperty("gift_type")
        GiftType giftType,

        @JsonProperty("cash_amount")
        Long cashAmount,

        Integer qty,

        @JsonProperty("expected_total_amount")
        Long expectedTotalAmount,

        @JsonProperty("transfer_count")
        int transferCount,

        @JsonProperty("first_transfer_date")
        LocalDate firstTransferDate,

        @JsonProperty("last_transfer_date")
        LocalDate lastTransferDate
) {}
