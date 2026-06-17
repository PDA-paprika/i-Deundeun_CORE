package com.iduenduen.coreservice.domain.upload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlResponse {

    @JsonProperty("upload_url")
    private String uploadUrl;

    @JsonProperty("file_url")
    private String fileUrl;

    @JsonProperty("expires_in")
    private int expiresIn;
}
