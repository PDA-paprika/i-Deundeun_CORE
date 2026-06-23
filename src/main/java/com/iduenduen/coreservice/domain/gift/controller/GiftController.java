package com.iduenduen.coreservice.domain.gift.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateRequest;
import com.iduenduen.coreservice.domain.gift.dto.GiftTitleUpdateRequest;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractCreateResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractDetailResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftContractListResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftPreviewResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftSummaryResponse;
import com.iduenduen.coreservice.domain.gift.dto.GiftTransferHistoryResponse;
import com.iduenduen.coreservice.domain.gift.service.GiftService;

@Tag(name = "Gift", description = "증여 API")
@RestController
@RequestMapping("/gifts")
@RequiredArgsConstructor
public class GiftController {

	private final GiftService giftService;

	@Operation(summary = "증여 예상 정보 계산", description = "증여 등록 전 예상 정보를 계산합니다.")
	@PostMapping("/{childId}/preview")
	public ResponseEntity<ApiResponse<GiftPreviewResponse>> preview(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId,
		@RequestBody @Valid GiftContractCreateRequest request) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.preview(parentId, childId, request));
	}

	@Operation(summary = "증여 계약 등록", description = "자녀에게 증여 계약을 등록합니다.")
	@PostMapping("/{childId}/contracts")
	public ResponseEntity<ApiResponse<GiftContractCreateResponse>> registerGiftContract(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId,
		@RequestBody @Valid GiftContractCreateRequest request) {
		return ApiResponse.success(SuccessStatus.GIFT_CONTRACT_CREATE_SUCCESS,
			giftService.registerGiftContract(parentId, childId, request));
	}

	@Operation(summary = "증여 계약 목록 조회", description = "자녀별 증여 계약 목록을 조회합니다.")
	@GetMapping("/{childId}/contracts")
	public ResponseEntity<ApiResponse<GiftContractListResponse>> getGiftContracts(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.getGiftContracts(parentId, childId));
	}

	@Operation(summary = "자녀별 이체 내역 조회", description = "자녀의 전체 이체 내역을 조회합니다.")
	@GetMapping("/{childId}/transfers")
	public ResponseEntity<ApiResponse<GiftTransferHistoryResponse>> getTransfersByChild(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.getTransfersByChild(parentId, childId));
	}

	@Operation(summary = "증여 현황 요약 조회", description = "자녀의 총 증여액 및 연도별 증여액을 조회합니다.")
	@GetMapping("/{childId}/summary")
	public ResponseEntity<ApiResponse<GiftSummaryResponse>> getGiftSummary(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.getGiftSummary(parentId, childId));
	}

	@Operation(summary = "증여 계약 상세 조회", description = "증여 계약 상세 정보 및 이체 목록을 조회합니다.")
	@GetMapping("/contracts/{contractId}")
	public ResponseEntity<ApiResponse<GiftContractDetailResponse>> getGiftContractDetail(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long contractId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.getGiftContractDetail(parentId, contractId));
	}

	@Operation(summary = "증여 계약 메모 수정", description = "증여 계약의 메모(title)를 수정합니다.")
	@PatchMapping("/contracts/{contractId}/title")
	public ResponseEntity<ApiResponse<Void>> updateContractTitle(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long contractId,
		@RequestBody @Valid GiftTitleUpdateRequest request) {
		giftService.updateContractTitle(parentId, contractId, request.getTitle());
		return ApiResponse.success(SuccessStatus.GIFT_CONTRACT_TITLE_UPDATE_SUCCESS, null);
	}

	@Operation(summary = "증여 계약 취소", description = "증여 계약을 취소합니다. DRAFT 또는 ACTIVE 상태만 취소 가능합니다.")
	@PatchMapping("/contracts/{contractId}/cancel")
	public ResponseEntity<ApiResponse<Void>> cancelGiftContract(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long contractId) {
		giftService.cancelGiftContract(parentId, contractId);
		return ApiResponse.success(SuccessStatus.GIFT_CONTRACT_CANCEL_SUCCESS, null);
	}

	@Operation(summary = "부모 전체 이체 내역 조회", description = "내가 보낸 전체 이체 내역을 조회합니다.")
	@GetMapping("/transfers")
	public ResponseEntity<ApiResponse<GiftTransferHistoryResponse>> getTransfersByParent(
		@AuthenticationPrincipal Long parentId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, giftService.getTransfersByParent(parentId));
	}
}