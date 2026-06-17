package com.iduenduen.coreservice.domain.upload.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.upload.dto.PresignedUrlRequest;
import com.iduenduen.coreservice.domain.upload.dto.PresignedUrlResponse;
import com.iduenduen.coreservice.domain.upload.service.UploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(@RequestBody @jakarta.validation.Valid PresignedUrlRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, uploadService.generatePresignedUrl(request));
    }
}
