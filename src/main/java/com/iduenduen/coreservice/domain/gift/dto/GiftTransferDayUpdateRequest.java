package com.iduenduen.coreservice.domain.gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GiftTransferDayUpdateRequest(
    @NotNull
    @Min(1)
    @Max(28)
    @JsonProperty("transfer_day")
    Integer transferDay
) {}
