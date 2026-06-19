package com.iduenduen.coreservice.domain.gift.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateRequest;
import com.iduenduen.coreservice.domain.gift.dto.GiftPreviewResponse;
import com.iduenduen.coreservice.domain.gift.service.GiftService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Gift", description = "증여 API")
@RestController
@RequestMapping("/gifts")
@RequiredArgsConstructor
public class GiftPreviewController {

    private final GiftService giftService;

    @Operation(summary = "증여 예상 정보 계산", description = "증여 등록 전 예상 정보를 계산합니다.")
    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<GiftPreviewResponse>> preview(
            @AuthenticationPrincipal Long parentId,
            @RequestBody @Valid GiftContractCreateRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.preview(parentId, request));
    }
}