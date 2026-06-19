package com.iduenduen.coreservice.domain.executionGoalLink.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;

import java.time.LocalDateTime;

public record GoalExecutionResponse(

    Long id,

    @JsonProperty("event_type")
    EtfEventType eventType,

    @JsonProperty("external_etf_id")
    String externalEtfId,

    @JsonProperty("qty_delta")
    int qtyDelta,

    long price,

    @JsonProperty("child_id")
    Long childId,

    String memo,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("occurred_at")
    LocalDateTime occurredAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("linked_at")
    LocalDateTime linkedAt
) {
    public static GoalExecutionResponse of(ExecutionGoalLink link, AccountEtfHistory history) {
        return new GoalExecutionResponse(
            link.getId(),
            history.getEventType(),
            history.getExternalEtfId(),
            history.getQtyDelta(),
            history.getPrice(),
            link.getChildId(),
            link.getMemo(),
            history.getOccurredAt(),
            link.getLinkedAt()
        );
    }
}
