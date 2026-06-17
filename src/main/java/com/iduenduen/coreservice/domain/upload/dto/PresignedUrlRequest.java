package com.iduenduen.coreservice.domain.upload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUrlRequest {

    @JsonProperty("file_type")
    @jakarta.validation.constraints.NotBlank
    private String fileType;

    @JsonProperty("content_type")
    @jakarta.validation.constraints.NotBlank
    private String contentType;

    @JsonProperty("file_name")
    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(max = 255)
    private String fileName;
}
