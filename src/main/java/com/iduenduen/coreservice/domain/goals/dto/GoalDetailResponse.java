package com.iduenduen.coreservice.domain.goals.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.goals.entity.Goal;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;

public record GoalDetailResponse(
    @JsonProperty("goal_id")
    Long goalId,

    @JsonProperty("goal_type1")
    GoalType goalType1,

    @JsonProperty("goal_type2")
    Integer goalType2,

    @JsonProperty("goal_type3")
    Integer goalType3,

    String name,

    @JsonProperty("target_amount")
    Long targetAmount,

    @JsonProperty("target_date")
    LocalDate targetDate,

    @JsonProperty("achievement_rate")
    BigDecimal achievementRate,

    @JsonProperty("remaining_period")
    String remainingPeriod,

    @JsonProperty("recommended_amount")
    Long recommendedAmount,

    @JsonProperty("recommendation_note")
    String recommendationNote,

    GoalStatus status
) {
    public static GoalDetailResponse from(Goal goal) {
        return new GoalDetailResponse(
            goal.getId(),
            goal.getGoalType1(),
            goal.getGoalType2(),
            goal.getGoalType3(),
            goal.getName(),
            goal.getTargetAmount(),
            goal.getTargetDate(),
            goal.getAchievedPct(),
            "D-" + ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate()),
            goal.getRecommendedAmount(),
            goal.getRecommendationNote(),
            goal.getStatus()
        );
    }
}
