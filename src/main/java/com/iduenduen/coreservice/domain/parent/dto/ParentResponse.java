package com.iduenduen.coreservice.domain.parent.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParentResponse {

    private Long id;
    private String name;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String relation;
    private String region;

    @JsonProperty("child_count")
    private Integer childCount;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("income_level")
    private String incomeLevel;

    @JsonProperty("asset_range")
    private String assetRange;

    @JsonProperty("education_heat")
    private Integer educationHeat;

    @JsonProperty("dual_income")
    private Boolean dualIncome;

    @JsonProperty("cluster_value")
    private Integer clusterValue;

    @JsonProperty("selected_child_id")
    private String selectedChildId;
}
