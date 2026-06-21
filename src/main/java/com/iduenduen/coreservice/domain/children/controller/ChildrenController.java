package com.iduenduen.coreservice.domain.children.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.children.dto.AllowanceStatusResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateRequest;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenDetailResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenListResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenUpdateRequest;
import com.iduenduen.coreservice.domain.children.service.ChildrenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Children", description = "자녀 관리 API")
@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildrenController {

	private final ChildrenService childrenService;

	@Operation(
		summary = "자녀 목록 조회",
		description = """
			로그인한 부모의 자녀 목록을 조회합니다.

			삭제되지 않은 자녀만 반환하며, 만나이는 서버에서 계산하여 응답합니다.""")
	@GetMapping
	public ResponseEntity<ApiResponse<ChildrenListResponse>> getChildren(
		@AuthenticationPrincipal Long parentId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, childrenService.getChildren(parentId));
	}

	@Operation(
		summary = "자녀 단건 조회",
		description = """
			자녀 ID로 특정 자녀의 상세 정보를 조회합니다.

			본인 자녀가 아닌 경우 404를 반환합니다.""")
	@GetMapping("/{childId}")
	public ResponseEntity<ApiResponse<ChildrenDetailResponse>> getChild(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, childrenService.getChild(parentId, childId));
	}

	@Operation(
		summary = "자녀 삭제",
		description = "자녀를 삭제합니다. 실제 데이터는 보존되며 소프트 딜리트 처리됩니다.")
	@DeleteMapping("/{childId}")
	public ResponseEntity<ApiResponse<Void>> deleteChild(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		childrenService.deleteChild(parentId, childId);
		return ApiResponse.success(SuccessStatus.CHILDREN_DELETE_SUCCESS);
	}

	@Operation(
		summary = "자녀 정보 수정",
		description = """
			자녀 정보를 수정합니다.

			모든 필드는 선택사항이며, 전달된 필드만 업데이트됩니다. 이름 변경 시 같은 부모 아래 중복 이름은 허용되지 않습니다.""")
	@PutMapping("/{childId}")
	public ResponseEntity<ApiResponse<ChildrenDetailResponse>> updateChild(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId,
		@RequestBody @Valid ChildrenUpdateRequest request) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200, childrenService.updateChild(parentId, childId, request));
	}

	@Operation(summary = "아동수당 연결", description = "자녀의 증권 계좌를 아동수당 수령 계좌로 연결합니다.")
	@PostMapping("/{childId}/allowance/connect")
	public ResponseEntity<ApiResponse<Void>> connectAllowance(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		childrenService.connectAllowance(parentId, childId);
		return ApiResponse.success(SuccessStatus.CHILDREN_ALLOWANCE_CONNECT_SUCCESS);
	}

	@Operation(summary = "아동수당 해지", description = "자녀의 아동수당 연결을 해지합니다.")
	@DeleteMapping("/{childId}/allowance")
	public ResponseEntity<ApiResponse<Void>> disconnectAllowance(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		childrenService.disconnectAllowance(parentId, childId);
		return ApiResponse.success(SuccessStatus.CHILDREN_ALLOWANCE_DISCONNECT_SUCCESS);
	}

	@Operation(summary = "아동수당 해지", description = "자녀의 아동수당 연결을 해지합니다.")
	@PatchMapping("/{childId}/allowance/disconnect")
	public ResponseEntity<ApiResponse<Void>> disconnectAllowancePatch(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		childrenService.disconnectAllowance(parentId, childId);
		return ApiResponse.success(SuccessStatus.CHILDREN_ALLOWANCE_DISCONNECT_SUCCESS);
	}

	@Operation(summary = "아동수당 연결 상태 조회", description = "자녀의 아동수당 연결 여부를 조회합니다.")
	@GetMapping("/{childId}/allowance")
	public ResponseEntity<ApiResponse<AllowanceStatusResponse>> getAllowanceStatus(
		@AuthenticationPrincipal Long parentId,
		@PathVariable Long childId) {
		return ApiResponse.success(SuccessStatus.SUCCESS_200,
			childrenService.getAllowanceStatus(parentId, childId));
	}

	@Operation(
		summary = "자녀 등록",
		description = """
			새로운 자녀를 등록합니다.

			같은 부모 아래 동일한 이름의 자녀는 등록할 수 없으며, 생년월일은 오늘 이전 날짜만 허용됩니다.""")
	@PostMapping
	public ResponseEntity<ApiResponse<ChildrenCreateResponse>> registerChild(
		@AuthenticationPrincipal Long parentId,
		@RequestBody @Valid ChildrenCreateRequest request) {
		return ApiResponse.success(SuccessStatus.CHILDREN_REGISTER_SUCCESS,
			childrenService.registerChild(parentId, request));
	}
}