package com.iduenduen.coreservice.domain.parent.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.parent.dto.ParentFrequencyRequest;
import com.iduenduen.coreservice.domain.parent.dto.ParentFrequencyResponse;
import com.iduenduen.coreservice.domain.parent.service.ParentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Parents", description = "부모 통계 API")
@RestController
@RequestMapping("/parents")
@RequiredArgsConstructor
public class ParentStatsController {
    private final ParentService parentService;

    @Operation(summary = "클러스터별 목표 유형 빈도 조회")
    @PostMapping("/frequency")
    public ResponseEntity<ApiResponse<List<ParentFrequencyResponse>>> getFrequency(
            @RequestBody @Valid ParentFrequencyRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, parentService.getFrequency(request));
    }
}
