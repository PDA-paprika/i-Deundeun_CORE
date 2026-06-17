package com.iduenduen.coreservice.domain.children.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.children.dto.AllowanceConnectResponse;
import com.iduenduen.coreservice.domain.children.dto.AllowanceStatusResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateRequest;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenDetailResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenListResponse;
import com.iduenduen.coreservice.domain.children.dto.ChildrenUpdateRequest;
import com.iduenduen.coreservice.domain.children.entity.Children;
import com.iduenduen.coreservice.domain.children.enums.CreatedVia;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildrenService {

	private final ChildrenRepository childrenRepository;
	private final ParentRepository parentRepository;

	public ChildrenListResponse getChildren(Long parentId) {
		return ChildrenListResponse.from(childrenRepository.findAllByParentIdAndDeletedAtIsNull(parentId));
	}

	public ChildrenDetailResponse getChild(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));
		return ChildrenDetailResponse.from(child);
	}

	@Transactional
	public void deleteChild(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));
		child.softDelete();
	}

	@Transactional
	public ChildrenDetailResponse updateChild(Long parentId, Long childId, ChildrenUpdateRequest request) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		if (request.name() != null && !request.name().equals(child.getName())
			&& childrenRepository.existsByParentIdAndNameAndDeletedAtIsNullAndIdNot(parentId, request.name(), childId)) {
			throw new GeneralException(ErrorStatus.CHILDREN_DUPLICATE_NAME);
		}

		child.update(request.name(), request.birthDate(), request.gender(),
			request.securitiesAccount(), request.profileImageUrl());
		return ChildrenDetailResponse.from(child);
	}

	@Transactional
	public AllowanceConnectResponse connectAllowance(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		if (child.isAllowanceLinked()) {
			throw new GeneralException(ErrorStatus.CHILDREN_ALLOWANCE_ALREADY_LINKED);
		}

		child.linkAllowance();
		return new AllowanceConnectResponse(child.getSecuritiesAccount());
	}

	public AllowanceStatusResponse getAllowanceStatus(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		return new AllowanceStatusResponse(child.isAllowanceLinked(), child.getSecuritiesAccount());
	}

	@Transactional
	public ChildrenCreateResponse registerChild(Long parentId, ChildrenCreateRequest request) {
		parentRepository.findByIdAndDeletedAtIsNull(parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_NOT_FOUND));

		if (childrenRepository.existsByParentIdAndNameAndDeletedAtIsNull(parentId, request.name())) {
			throw new GeneralException(ErrorStatus.CHILDREN_DUPLICATE_NAME);
		}

		Children children = Children.builder()
			.parentId(parentId)
			.name(request.name())
			.birthDate(request.birthDate())
			.gender(request.gender())
			.birthOrder(request.birthOrder())
			.securitiesAccount(request.securitiesAccount())
			.profileImageUrl(request.profileImageUrl())
			.createdVia(CreatedVia.MYPAGE)
			.build();

		Children registered = childrenRepository.save(children);
		return ChildrenCreateResponse.from(registered);
	}
}