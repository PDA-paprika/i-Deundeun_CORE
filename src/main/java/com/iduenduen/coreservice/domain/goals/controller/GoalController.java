package com.iduenduen.coreservice.domain.goals.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.goals.dto.GoalCreateRequest;
import com.iduenduen.coreservice.domain.goals.dto.GoalCreateResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalDetailResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalListResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalPreviewRequest;
import com.iduenduen.coreservice.domain.goals.dto.GoalPreviewResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalUpdateRequest;
import com.iduenduen.coreservice.domain.goals.dto.GoalUpdateResponse;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.service.GoalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Goals", description = "투자 목표 API")
@RestController
@RequestMapping("/children/{childId}/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "자녀 목표 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<GoalListResponse>> getGoals(
        @AuthenticationPrincipal Long parentId,
        @PathVariable Long childId,
        @RequestParam(defaultValue = "ACTIVE") GoalStatus status) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, goalService.getGoals(parentId, childId, status));
    }

    @Operation(summary = "목표 단건 조회")
    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<GoalDetailResponse>> getGoal(
        @AuthenticationPrincipal Long parentId,
        @PathVariable Long childId,
        @PathVariable Long goalId) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, goalService.getGoal(parentId, childId, goalId));
    }

    @Operation(summary = "목표 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<GoalCreateResponse>> createGoal(
        @AuthenticationPrincipal Long parentId,
        @PathVariable Long childId,
        @RequestBody @Valid GoalCreateRequest request) {
        return ApiResponse.success(SuccessStatus.GOAL_CREATE_SUCCESS, goalService.createGoal(parentId, childId, request));
    }

    @Operation(summary = "목표 수정")
    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse<GoalUpdateResponse>> updateGoal(
        @AuthenticationPrincipal Long parentId,
        @PathVariable Long childId,
        @PathVariable Long goalId,
        @RequestBody @Valid GoalUpdateRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, goalService.updateGoal(parentId, childId, goalId, request));
    }

    @Operation(summary = "목표 삭제")
    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
        @AuthenticationPrincipal Long parentId,
        @PathVariable Long childId,
        @PathVariable Long goalId) {
        goalService.deleteGoal(parentId, childId, goalId);
        return ApiResponse.success(SuccessStatus.GOAL_DELETE_SUCCESS);
    }

    @Operation(summary = "달성률 미리보기", description = "목표 수정 시 변경 예상 달성률을 미리 계산합니다. DB에 저장되지 않습니다.")
    @PostMapping("/{goalId}/preview")
    public ResponseEntity<ApiResponse<GoalPreviewResponse>> previewGoal(
        @AuthenticationPrincipal Long parentId,
        @PathVariable Long childId,
        @PathVariable Long goalId,
        @RequestBody GoalPreviewRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200,
            goalService.previewGoal(parentId, childId, goalId, request.targetAmount(), request.targetDate()));
    }
}
