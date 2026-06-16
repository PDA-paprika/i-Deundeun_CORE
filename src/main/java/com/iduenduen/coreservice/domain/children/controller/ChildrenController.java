package com.iduenduen.coreservice.domain.children.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.status.SuccessStatus;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateRequest;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateResponse;
import com.iduenduen.coreservice.domain.children.service.ChildrenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildrenController {

	private final ChildrenService childrenService;

	@PostMapping
	public ResponseEntity<ApiResponse<ChildrenCreateResponse>> registerChild(
		@AuthenticationPrincipal Long parentId,
		@RequestBody @Valid ChildrenCreateRequest request) {
		return ApiResponse.success(SuccessStatus.CHILDREN_REGISTER_SUCCESS,
			childrenService.registerChild(parentId, request));
	}
}