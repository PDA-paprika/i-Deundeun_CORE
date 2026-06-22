package com.iduenduen.coreservice.domain.goals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalName;

public record AssistantGoalNameCreateResponse(
    @JsonProperty("id")
    Long id
) {
    public static AssistantGoalNameCreateResponse from(AssistantGoalName entity) {
        return new AssistantGoalNameCreateResponse(entity.getId());
    }
}
