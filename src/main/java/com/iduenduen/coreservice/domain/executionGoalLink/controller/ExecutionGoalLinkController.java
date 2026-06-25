package com.iduenduen.coreservice.domain.executionGoalLink.controller;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalExecutionRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalHoldingsResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.MoveRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.UnlinkedExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.service.ExecutionGoalLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/execution-links")
@RequiredArgsConstructor
public class ExecutionGoalLinkController {

    private final ExecutionGoalLinkService executionGoalLinkService;

    @GetMapping("/unlinked")
    public ResponseEntity<ApiResponse<List<UnlinkedExecutionResponse>>> getUnlinked(
            @AuthenticationPrincipal Long parentId
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS,
            executionGoalLinkService.getUnlinked(parentId));
    }

    @PutMapping("/{linkId}/move")
    public ResponseEntity<ApiResponse<Void>> move(
            @AuthenticationPrincipal Long parentId,
            @PathVariable Long linkId,
            @RequestBody @Valid MoveRequest req
    ) {
        executionGoalLinkService.moveLink(parentId, linkId, req);
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS);
    }

    @PostMapping("/by-goal")
    public ResponseEntity<ApiResponse<List<GoalExecutionResponse>>> getByGoal(
            @RequestBody @Valid GoalExecutionRequest req
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS,
            executionGoalLinkService.getByGoal(req.goalId(), req.childId()));
    }

    @PostMapping("/holdings")
    public ResponseEntity<ApiResponse<GoalHoldingsResponse>> getGoalHoldings(
            @RequestBody @Valid GoalExecutionRequest req
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS,
            executionGoalLinkService.getGoalHoldings(req.goalId(), req.childId()));
    }
}
