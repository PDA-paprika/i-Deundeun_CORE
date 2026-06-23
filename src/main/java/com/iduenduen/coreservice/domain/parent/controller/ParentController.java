package com.iduenduen.coreservice.domain.parent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.parent.dto.ParentResponse;
import com.iduenduen.coreservice.domain.parent.dto.ParentUpdateRequest;
import com.iduenduen.coreservice.domain.parent.dto.ParentUpdateResponse;
import com.iduenduen.coreservice.domain.parent.dto.SelectedChildRequest;
import com.iduenduen.coreservice.domain.parent.dto.SelectedChildResponse;
import com.iduenduen.coreservice.domain.parent.dto.WizardProfileRequest;
import com.iduenduen.coreservice.domain.parent.service.ParentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/parents/me")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @GetMapping
    public ResponseEntity<ApiResponse<ParentResponse>> getMe(@AuthenticationPrincipal Long parentId) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, parentService.getMe(parentId));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ParentUpdateResponse>> updateMe(
            @AuthenticationPrincipal Long parentId,
            @RequestBody ParentUpdateRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, parentService.updateMe(parentId, request));
    }

    @PutMapping("/selected-child")
    public ResponseEntity<ApiResponse<SelectedChildResponse>> updateSelectedChild(
            @AuthenticationPrincipal Long parentId,
            @RequestBody SelectedChildRequest request) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, parentService.updateSelectedChild(parentId, request));
    }

    @PatchMapping("/wizard-profile")
    public ResponseEntity<ApiResponse<Void>> updateWizardProfile(
            @AuthenticationPrincipal Long parentId,
            @RequestBody WizardProfileRequest request) {
        parentService.updateWizardProfile(parentId, request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200);
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal Long parentId) {
        parentService.withdraw(parentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<Object>> getHoldings(
            @AuthenticationPrincipal Long parentId,
            @RequestParam(required = false, defaultValue = "eval_amount_desc") String sort,
            @RequestParam(required = false) Integer limit) {
        // TODO: ETF Server 연동 필요
        return ApiResponse.success(SuccessStatus.SUCCESS_200, null);
    }
}