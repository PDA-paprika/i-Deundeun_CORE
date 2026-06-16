package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SelectedChildResponse {

    @JsonProperty("selected_child_id")
    private String selectedChildId;
}
