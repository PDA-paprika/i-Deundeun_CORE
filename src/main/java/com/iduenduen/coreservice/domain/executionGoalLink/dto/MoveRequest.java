package com.iduenduen.coreservice.domain.executionGoalLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MoveRequest(

    @JsonProperty("child_id")
    Long childId,

    @JsonProperty("goal_id")
    Long goalId
) {}
