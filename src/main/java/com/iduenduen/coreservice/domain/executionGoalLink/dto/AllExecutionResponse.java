package com.iduenduen.coreservice.domain.executionGoalLink.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;

import java.time.LocalDateTime;

public record AllExecutionResponse(

    Long id,

    @JsonProperty("etf_history_id")
    Long etfHistoryId,

    @JsonProperty("etf_id")
    Long etfId,

    @JsonProperty("etf_name")
    String etfName,

    int qty,

    long price,

    @JsonProperty("child_id")
    Long childId,

    @JsonProperty("goal_id")
    Long goalId,

    @JsonProperty("logo_img")
    String logoImg,

    @JsonProperty("event_type")
    EtfEventType eventType,

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @JsonProperty("occurred_at")
    LocalDateTime occurredAt
) {
    public static AllExecutionResponse of(ExecutionGoalLink link, AccountEtfHistory history, String logoImg) {
        return new AllExecutionResponse(
            link.getId(),
            history.getId(),
            history.getEtfId(),
            history.getEtfNameSnapshot(),
            link.getQty(),
            history.getPrice(),
            link.getChildId(),
            link.getGoalId(),
            logoImg,
            history.getEventType(),
            history.getOccurredAt()
        );
    }
}
