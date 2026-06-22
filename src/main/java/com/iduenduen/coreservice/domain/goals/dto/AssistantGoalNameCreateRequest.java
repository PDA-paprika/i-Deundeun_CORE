package com.iduenduen.coreservice.domain.goals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record AssistantGoalNameCreateRequest(
    @NotNull
    @JsonProperty("goal_type1")
    Integer goalType1,

    @NotNull
    @JsonProperty("goal_type2")
    Integer goalType2,

    @NotNull
    @JsonProperty("goal_type3")
    Integer goalType3,

    @JsonProperty("name")
    String name
) {}
