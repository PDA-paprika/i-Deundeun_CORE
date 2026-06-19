package com.iduenduen.coreservice.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EtfSellRequest(

    @NotNull
    @JsonProperty("account_id")
    Long accountId,

    @NotNull
    @JsonProperty("parent_id")
    Long parentId,

    @NotNull
    @JsonProperty("child_id")
    Long childId,

    @NotNull
    @JsonProperty("goal_id")
    Long goalId,

    @NotNull
    @JsonProperty("external_etf_id")
    String externalEtfId,

    @NotNull
    @Positive
    @JsonProperty("qty")
    Integer qty,

    @NotNull
    @Positive
    Long price,

    String memo,

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("occurred_at")
    LocalDateTime occurredAt
) {}
