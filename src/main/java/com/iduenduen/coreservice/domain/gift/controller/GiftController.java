package com.iduenduen.coreservice.domain.gift.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateRequest;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateResponse;
import com.iduenduen.coreservice.domain.gift.service.GiftService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Gift", description = "증여 API")
@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class GiftController {

	private final GiftService giftService;

	@Operation(summary = "증여 계약 등록", description = "자녀에게 증여 계약을 등록합니다.")
	@PostMapping("/{childId}/gift-contracts")
	public ResponseEntity<ApiResponse<GiftContractCreateResponse>> registerGiftContract(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId,
		@RequestBody @Valid GiftContractCreateRequest request) {
		return ApiResponse.success(SuccessStatus.GIFT_CONTRACT_CREATE_SUCCESS,
			giftService.registerGiftContract(parentId, childId, request));
	}
}