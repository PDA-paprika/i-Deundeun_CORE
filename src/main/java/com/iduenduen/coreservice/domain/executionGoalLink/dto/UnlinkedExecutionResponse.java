package com.iduenduen.coreservice.domain.executionGoalLink.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;

import java.time.LocalDateTime;

public record UnlinkedExecutionResponse(

    Long id,

    @JsonProperty("etf_history_id")
    Long etfHistoryId,

    @JsonProperty("event_type")
    EtfEventType eventType,

    @JsonProperty("etf_id")
    Long etfId,

    @JsonProperty("qty_delta")
    int qtyDelta,

    long price,

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @JsonProperty("occurred_at")
    LocalDateTime occurredAt,

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @JsonProperty("created_at")
    LocalDateTime createdAt
) {
    public static UnlinkedExecutionResponse of(ExecutionGoalLink link, AccountEtfHistory history) {
        return new UnlinkedExecutionResponse(
            link.getId(),
            history.getId(),
            history.getEventType(),
            history.getEtfId(),
            history.getQtyDelta(),
            history.getPrice(),
            history.getOccurredAt(),
            link.getCreatedAt()
        );
    }
}
