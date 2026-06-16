package com.iduenduen.coreservice.domain.children.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateRequest;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenListResponse;
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