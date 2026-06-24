package com.iduenduen.coreservice.domain.parent.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatsResponse {

    @JsonProperty("mode")
    private Double mode;

    @JsonProperty("mean")
    private Double mean;

    @JsonProperty("p25")
    private Double p25;

    @JsonProperty("p75")
    private Double p75;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("distribution")
    private List<Double> distribution;
}
