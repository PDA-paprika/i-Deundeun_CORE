package com.iduenduen.coreservice.domain.goals.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoalUpdateResponse(
    @JsonProperty("goal_id")
    Long goalId,

    @JsonProperty("achievement_rate")
    BigDecimal achievementRate
) {}
