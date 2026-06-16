package com.iduenduen.coreservice.domain.parent.controller;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<ParentResponse>> getMe() {
        ParentResponse response = parentService.getMe(getCurrentParentId());
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ParentUpdateResponse>> updateMe(@RequestBody ParentUpdateRequest request) {
        ParentUpdateResponse response = parentService.updateMe(getCurrentParentId(), request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }

    @PutMapping("/selected-child")
    public ResponseEntity<ApiResponse<SelectedChildResponse>> updateSelectedChild(
            @RequestBody SelectedChildRequest request) {
        SelectedChildResponse response = parentService.updateSelectedChild(getCurrentParentId(), request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }

    @PatchMapping("/wizard-profile")
    public ResponseEntity<ApiResponse<Void>> updateWizardProfile(@RequestBody WizardProfileRequest request) {
        parentService.updateWizardProfile(getCurrentParentId(), request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200);
    }

    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<Object>> getHoldings(
            @RequestParam(required = false, defaultValue = "eval_amount_desc") String sort,
            @RequestParam(required = false) Integer limit) {
        // TODO: ETF Server 연동 필요 (account_etf_holdings + etfs + etf_candles_1d)
        return ApiResponse.success(SuccessStatus.SUCCESS_200, null);
    }

    private Long getCurrentParentId() {
        // TODO: 인증 구현 후 SecurityContext에서 로그인한 parent id로 교체
        throw new UnsupportedOperationException("인증이 아직 구현되지 않았습니다.");
    }
}
