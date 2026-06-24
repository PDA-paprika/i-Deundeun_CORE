package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClusterTrainResponse {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("cluster_value")
    private Integer clusterValue;
}
