package com.iduenduen.coreservice.domain.executionGoalLink.controller;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.account.dto.EtfTradeNotificationRequest;
import com.iduenduen.coreservice.domain.account.service.AccountService;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.LinkRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.UnlinkedExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.service.ExecutionGoalLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/execution-links")
@RequiredArgsConstructor
public class ExecutionGoalLinkController {

    private final AccountService accountService;
    private final ExecutionGoalLinkService executionGoalLinkService;

    @PostMapping("/trade")
    public ResponseEntity<ApiResponse<Long>> recordTrade(
        @RequestBody @Valid EtfTradeNotificationRequest req
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_CREATED, accountService.recordTrade(req));
    }

    @GetMapping("/unlinked")
    public ResponseEntity<ApiResponse<List<UnlinkedExecutionResponse>>> getUnlinked(
        @RequestParam("parent_id") Long parentId
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS,
            executionGoalLinkService.getUnlinked(parentId));
    }

    @PutMapping("/{linkId}/link")
    public ResponseEntity<ApiResponse<Void>> link(
        @PathVariable Long linkId,
        @RequestBody @Valid LinkRequest req
    ) {
        executionGoalLinkService.link(linkId, req);
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS);
    }

    @GetMapping("/by-goal/{goalId}")
    public ResponseEntity<ApiResponse<List<GoalExecutionResponse>>> getByGoal(
        @PathVariable Long goalId
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_SUCCESS,
            executionGoalLinkService.getByGoal(goalId));
    }
}
