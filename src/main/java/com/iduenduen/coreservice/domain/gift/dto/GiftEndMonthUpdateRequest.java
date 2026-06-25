package com.iduenduen.coreservice.domain.gift.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.YearMonth;

public record GiftEndMonthUpdateRequest(
    @NotNull
    @JsonFormat(pattern = "yyyy.MM")
    @JsonProperty("end_month")
    YearMonth endMonth
) {}
