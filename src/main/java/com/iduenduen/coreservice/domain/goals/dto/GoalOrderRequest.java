package com.iduenduen.coreservice.domain.goals.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;

public record GoalOrderRequest(
    @NotEmpty @JsonProperty("ids") List<Long> ids
) {}
