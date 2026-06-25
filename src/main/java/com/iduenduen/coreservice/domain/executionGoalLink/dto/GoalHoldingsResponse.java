package com.iduenduen.coreservice.domain.executionGoalLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GoalHoldingsResponse {

    @JsonProperty("goal_id")
    private Long goalId;

    @JsonProperty("child_id")
    private Long childId;

    private List<HoldingDto> holdings;

    @Getter
    @Builder
    public static class HoldingDto {

        @JsonProperty("etf_id")
        private Long etfId;

        @JsonProperty("etf_name")
        private String etfName;

        private int qty;
    }
}
