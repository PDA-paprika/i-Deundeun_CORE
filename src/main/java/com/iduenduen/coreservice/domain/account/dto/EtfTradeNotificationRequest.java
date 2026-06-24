package com.iduenduen.coreservice.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.enums.ReferenceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EtfTradeNotificationRequest(

    @NotNull
    @JsonProperty("account_id")
    Long accountId,

    @NotNull
    @JsonProperty("parent_id")
    Long parentId,

    @NotNull
    @JsonProperty("event_type")
    EtfEventType eventType,

    @JsonProperty("etf_id")
    Long etfId,

    @JsonProperty("etf_name")
    String etfName,

    @NotNull
    @Positive
    Integer qty,

    @NotNull
    @Positive
    Long price,

    @JsonProperty("reference_id")
    String referenceId,

    @JsonProperty("reference_type")
    ReferenceType referenceType,

    String memo,

    @NotNull
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @JsonProperty("occurred_at")
    LocalDateTime occurredAt
) {}
