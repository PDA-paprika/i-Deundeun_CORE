package com.iduenduen.coreservice.domain.goals.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.goals.dto.GoalOptionsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Goals", description = "투자 목표 API")
@RestController
@RequestMapping("/goals")
public class GoalOptionController {

    @Operation(summary = "목표 유형 옵션 조회", description = "목표 설정 드롭다운에 사용할 목표 유형 목록을 반환합니다.")
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<GoalOptionsResponse>> getOptions() {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, GoalOptionsResponse.of());
    }
}
