package com.iduenduen.coreservice.domain.goals.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoalPreviewResponse(
    @JsonProperty("current_achievement_rate")
    BigDecimal currentAchievementRate,

    @JsonProperty("preview_achievement_rate")
    BigDecimal previewAchievementRate
) {}
