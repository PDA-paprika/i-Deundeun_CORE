package com.iduenduen.coreservice.domain.goals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalName;

public record AssistantGoalNameGetResponse(
    @JsonProperty("id")
    Long id,
    @JsonProperty("goal_type1")
    int goalType1,
    @JsonProperty("goal_type2")
    int goalType2,
    @JsonProperty("goal_type3")
    int goalType3,
    @JsonProperty("name")
    String name
) {
    public static AssistantGoalNameGetResponse from(AssistantGoalName entity) {
        return new AssistantGoalNameGetResponse(
            entity.getId(),
            entity.getGoalType1().getCode(),
            entity.getGoalType2(),
            entity.getGoalType3(),
            entity.getName()
        );
    }
}
