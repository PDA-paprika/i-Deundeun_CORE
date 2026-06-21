package com.iduenduen.coreservice.domain.goals.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;

import jakarta.validation.constraints.Positive;

public record GoalUpdateRequest(
    @JsonProperty("goal_type1")
    GoalType goalType1,

    @JsonProperty("goal_type2")
    Integer goalType2,

    @JsonProperty("goal_type3")
    Integer goalType3,

    String name,

    @Positive
    @JsonProperty("target_amount")
    Long targetAmount,

    @JsonFormat(pattern = "yyyy.MM.dd")
    @JsonProperty("target_date")
    LocalDate targetDate
) {}
