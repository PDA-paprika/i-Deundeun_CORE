package com.iduenduen.coreservice.domain.goals.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GoalPreviewRequest(
    @JsonProperty("target_amount")
    Long targetAmount,

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("target_date")
    LocalDate targetDate
) {}
