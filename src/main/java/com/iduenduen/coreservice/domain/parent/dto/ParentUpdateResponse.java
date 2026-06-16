package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParentUpdateResponse {

    @JsonProperty("parent_id")
    private String parentId;
}
