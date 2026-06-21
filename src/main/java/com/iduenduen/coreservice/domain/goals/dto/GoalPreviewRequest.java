package com.iduenduen.coreservice.domain.goals.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.iduenduen.coreservice.common.util.FlexibleLocalDateDeserializer;

public record GoalPreviewRequest(
    @JsonProperty("target_amount")
    Long targetAmount,

    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @JsonProperty("target_date")
    LocalDate targetDate
) {}
