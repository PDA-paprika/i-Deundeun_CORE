package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WizardProfileRequest {

    @JsonProperty("monthly_household_income")
    private Integer monthlyHouseholdIncome;

    @JsonProperty("parent_economic_activity")
    private Integer parentEconomicActivity;

    @JsonProperty("education_level")
    private Integer educationLevel;
}
