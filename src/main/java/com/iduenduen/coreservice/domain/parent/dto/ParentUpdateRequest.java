package com.iduenduen.coreservice.domain.parent.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParentUpdateRequest {

    private String name;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String relation;

    @JsonProperty("child_count")
    private Integer childCount;

    private String region;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;
}
