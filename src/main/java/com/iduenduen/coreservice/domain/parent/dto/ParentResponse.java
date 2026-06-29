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

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String relation;
    private String region;

    @JsonProperty("child_count")
    private Integer childCount;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("monthly_household_income")
    private Integer monthlyHouseholdIncome;

    @JsonProperty("parent_economic_activity")
    private Integer parentEconomicActivity;

    @JsonProperty("education_level")
    private Integer educationLevel;

    @JsonProperty("cluster_value")
    private Integer clusterValue;

    @JsonProperty("selected_child_id")
    private Long selectedChildId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("cert_file_url")
    private String certFileUrl;
}
