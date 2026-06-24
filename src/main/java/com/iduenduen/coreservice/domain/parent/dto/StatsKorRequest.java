package com.iduenduen.coreservice.domain.parent.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatsKorRequest {

    @JsonProperty("goal_type1")
    private Integer goalType1;

    @JsonProperty("goal_type2")
    private Integer goalType2;

    @JsonProperty("goal_type3")
    private Integer goalType3;

    @JsonProperty("distribution")
    private List<Integer> distribution;
}
