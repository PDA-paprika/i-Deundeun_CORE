package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WizardProfileRequest {

    @JsonProperty("income_level")
    private String incomeLevel;

    @JsonProperty("asset_range")
    private String assetRange;

    @JsonProperty("education_heat")
    private Integer educationHeat;

    @JsonProperty("dual_income")
    private Boolean dualIncome;
}
