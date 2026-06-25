package com.iduenduen.coreservice.domain.executionGoalLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record GoalExecutionRequest(

    @NotNull
    @JsonProperty("goal_id")
    Long goalId,

    @NotNull
    @JsonProperty("child_id")
    Long childId
) {}
