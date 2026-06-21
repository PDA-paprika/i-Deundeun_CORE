package com.iduenduen.coreservice.domain.goals.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.iduenduen.coreservice.common.util.FlexibleLocalDateDeserializer;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GoalCreateRequest(
    @NotNull
    @JsonProperty("goal_type1")
    GoalType goalType1,

    @JsonProperty("goal_type2")
    Integer goalType2,

    @JsonProperty("goal_type3")
    Integer goalType3,

    String name,

    @NotNull
    @Positive
    @JsonProperty("target_amount")
    Long targetAmount,

    @NotNull
    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @JsonProperty("target_date")
    LocalDate targetDate
) {}
