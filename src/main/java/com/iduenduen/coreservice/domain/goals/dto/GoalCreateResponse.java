package com.iduenduen.coreservice.domain.goals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.goals.entity.Goal;

public record GoalCreateResponse(
    @JsonProperty("goal_id")
    Long goalId
) {
    public static GoalCreateResponse from(Goal goal) {
        return new GoalCreateResponse(goal.getId());
    }
}
