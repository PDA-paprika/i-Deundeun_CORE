package com.iduenduen.coreservice.domain.parent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.security.JwtProvider;
import com.iduenduen.coreservice.common.status.ErrorStatus;
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
    private final JwtProvider jwtProvider;

    @GetMapping
    public ResponseEntity<ApiResponse<ParentResponse>> getMe(
            @RequestHeader("Authorization") String authorization) {
        ParentResponse response = parentService.getMe(resolveParentId(authorization));
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ParentUpdateResponse>> updateMe(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ParentUpdateRequest request) {
        ParentUpdateResponse response = parentService.updateMe(resolveParentId(authorization), request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }

    @PutMapping("/selected-child")
    public ResponseEntity<ApiResponse<SelectedChildResponse>> updateSelectedChild(
            @RequestHeader("Authorization") String authorization,
            @RequestBody SelectedChildRequest request) {
        SelectedChildResponse response = parentService.updateSelectedChild(resolveParentId(authorization), request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200, response);
    }

    @PatchMapping("/wizard-profile")
    public ResponseEntity<ApiResponse<Void>> updateWizardProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody WizardProfileRequest request) {
        parentService.updateWizardProfile(resolveParentId(authorization), request);
        return ApiResponse.success(SuccessStatus.SUCCESS_200);
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(
            @RequestHeader("Authorization") String authorization) {
        parentService.withdraw(resolveParentId(authorization));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<Object>> getHoldings(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false, defaultValue = "eval_amount_desc") String sort,
            @RequestParam(required = false) Integer limit) {
        // TODO: ETF Server 연동 필요 (account_etf_holdings + etfs + etf_candles_1d)
        return ApiResponse.success(SuccessStatus.SUCCESS_200, null);
    }

    private Long resolveParentId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return jwtProvider.getParentId(authorization.substring("Bearer ".length()));
    }
}
