package com.iduenduen.coreservice.domain.upload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUrlRequest {

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("file_name")
    private String fileName;
}
