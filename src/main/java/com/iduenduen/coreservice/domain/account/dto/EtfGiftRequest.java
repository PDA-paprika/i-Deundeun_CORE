package com.iduenduen.coreservice.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EtfGiftRequest(

    @NotNull
    @JsonProperty("child_id")
    Long childId,

    @NotNull
    @JsonProperty("etf_id")
    Long etfId,

    @JsonProperty("etf_name")
    String etfName,

    @NotNull
    @Positive
    @JsonProperty("current_price")
    Long currentPrice,

    @Positive
    int qty,

    String memo
) {}
