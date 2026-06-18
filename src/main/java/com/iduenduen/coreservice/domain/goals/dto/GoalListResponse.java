package com.iduenduen.coreservice.domain.goals.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.goals.entity.Goal;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;

public record GoalListResponse(List<GoalItem> goals) {
    public record GoalItem(
        @JsonProperty("goal_id")
        Long goalId,

        @JsonProperty("goal_type1")
        GoalType goalType1,

        String name,

        @JsonProperty("target_amount")
        Long targetAmount,

        @JsonProperty("target_date")
        LocalDate targetDate,

        @JsonProperty("achievement_rate")
        BigDecimal achievementRate,

        @JsonProperty("remaining_period")
        String remainingPeriod,

        GoalStatus status
    ) {
        public static GoalItem from(Goal goal) {
            return new GoalItem(
                goal.getId(),
                goal.getGoalType1(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getAchievedPct(),
                remainingPeriod(goal.getTargetDate()),
                goal.getStatus()
            );
        }
        private static String remainingPeriod(LocalDate targetDate) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
            return days < 0 ? "만료" : "D-" + days;
        }
    }

    public static GoalListResponse from(List<Goal> goals) {
        return new GoalListResponse(goals.stream().map(GoalItem::from).toList());
    }
}
