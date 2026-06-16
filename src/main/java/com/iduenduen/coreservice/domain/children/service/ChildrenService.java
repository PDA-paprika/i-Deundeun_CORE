package com.iduenduen.coreservice.domain.children.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateRequest;
import com.iduenduen.coreservice.domain.children.dto.ChildrenCreateResponse;
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

	@Transactional
	public ChildrenCreateResponse registerChild(Long parentId, ChildrenCreateRequest request) {
		parentRepository.findByIdAndDeletedAtIsNull(parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_NOT_FOUND));

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