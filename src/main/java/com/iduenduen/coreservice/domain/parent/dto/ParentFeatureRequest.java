package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParentFeatureRequest {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("relation")
    private String relation;

    @JsonProperty("urban_flag")
    private Integer urbanFlag;

    @JsonProperty("parent_economic_activity")
    private Integer parentEconomicActivity;

    @JsonProperty("monthly_household_income")
    private Integer monthlyHouseholdIncome;

    @JsonProperty("education_level")
    private Integer educationLevel;

    @JsonProperty("child_count")
    private Integer childCount;
}
