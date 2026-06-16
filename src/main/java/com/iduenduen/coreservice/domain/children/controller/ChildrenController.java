package com.iduenduen.coreservice.domain.children.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.response.ApiResponse;
import com.iduenduen.coreservice.common.security.JwtProvider;
import com.iduenduen.coreservice.common.status.ErrorStatus;
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
	private final JwtProvider jwtProvider;

	@PostMapping
	public ResponseEntity<ApiResponse<ChildrenCreateResponse>> registerChild(
		@RequestHeader("Authorization") String authorization,
		@RequestBody @Valid ChildrenCreateRequest request) {
		Long parentId = resolveParentId(authorization);
		ChildrenCreateResponse response = childrenService.registerChild(parentId, request);
		return ApiResponse.success(SuccessStatus.CHILDREN_REGISTER_SUCCESS, response);
	}

	private Long resolveParentId(String authorization) {
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new GeneralException(ErrorStatus.UNAUTHORIZED);
		}
		return jwtProvider.getParentId(authorization.substring("Bearer ".length()));
	}
}