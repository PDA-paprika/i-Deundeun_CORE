package com.iduenduen.coreservice.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenValidationResponse {

    @JsonProperty("parent_id")
    private Long parentId;
}
