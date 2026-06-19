package com.iduenduen.coreservice.domain.account.controller;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.account.dto.AccountBalanceResponse;
import com.iduenduen.coreservice.domain.account.dto.AccountInfoResponse;
import com.iduenduen.coreservice.domain.account.dto.EtfTradeNotificationRequest;
import com.iduenduen.coreservice.domain.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "Account", description = "계좌 API")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    @Operation(summary = "내 계좌 조회", description = "로그인한 부모의 계좌 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AccountInfoResponse>> getMyAccount(
            @AuthenticationPrincipal Long parentId) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, accountService.getMyAccount(parentId));
    }

    @GetMapping("/balance")
    @Operation(summary = "예수금 조회", description = "D+0/1/2 예수금을 조회합니다.")
    public ResponseEntity<ApiResponse<AccountBalanceResponse>> getBalance(
            @RequestParam Long accountId) {
        return ApiResponse.success(SuccessStatus.SUCCESS_200, accountService.getBalance(accountId));
    }

    @PostMapping("/trade")
    public ResponseEntity<ApiResponse<Long>> recordTrade(
            @RequestBody @Valid EtfTradeNotificationRequest req
    ) {
        return ApiResponse.success(SuccessStatus.EXECUTION_LINK_CREATED, accountService.recordTrade(req));
    }
}