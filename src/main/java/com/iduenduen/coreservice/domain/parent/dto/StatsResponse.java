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

    @JsonProperty("q25")
    private Double q25;

    @JsonProperty("q75")
    private Double q75;

    @JsonProperty("comment")
    private String comment;
}
