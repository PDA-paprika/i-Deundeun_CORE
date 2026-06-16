package com.iduenduen.coreservice.domain.children.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.response.ApiResponse;
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

	@PostMapping
	public ResponseEntity<ApiResponse<ChildrenCreateResponse>> registerChild(
		@RequestBody @Valid ChildrenCreateRequest request) {
		ChildrenCreateResponse response = childrenService.registerChild(getCurrentParentId(), request);
		return ApiResponse.success(SuccessStatus.CHILDREN_REGISTER_SUCCESS, response);
	}


	//추후 로그인 구현 완료 후 변경 예정
	private Long getCurrentParentId() {
		var attrs = (org.springframework.web.context.request.ServletRequestAttributes)
			org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			throw new GeneralException(ErrorStatus.UNAUTHORIZED);
		}

		String parentId = attrs.getRequest().getHeader("X-Parent-Id");
		if (parentId == null || parentId.isBlank()) {
			throw new GeneralException(ErrorStatus.UNAUTHORIZED);
		}
		return Long.valueOf(parentId);
	}
}
