package com.iduenduen.coreservice.domain.goals.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameCreateRequest;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameCreateResponse;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameGetResponse;
import com.iduenduen.coreservice.domain.goals.service.AssistantGoalNameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "AssistantGoalNames", description = "AI 추천 목표명 API")
@RestController
@RequestMapping("/assistant-goal-names")
@RequiredArgsConstructor
public class AssistantGoalNameController {

    private final AssistantGoalNameService assistantGoalNameService;

    @Operation(summary = "목표 도우미 데이터 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AssistantGoalNameGetResponse>>> getList(
        @AuthenticationPrincipal Long parentId) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200,
            assistantGoalNameService.getList(parentId));
    }

    @Operation(summary = "목표 도우미 데이터 저장")
    @PostMapping
    public ResponseEntity<ApiResponse<AssistantGoalNameCreateResponse>> create(
        @AuthenticationPrincipal Long parentId,
        @RequestBody @Valid AssistantGoalNameCreateRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_201,
            assistantGoalNameService.create(parentId, request));
    }
}
