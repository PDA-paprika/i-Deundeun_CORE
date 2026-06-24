package com.iduenduen.coreservice.domain.children.service;

import java.util.List;

import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.children.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
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
	private final AccountRepository accountRepository;
	private final AccountEtfHoldingRepository accountEtfHoldingRepository;

	public ChildrenListResponse getChildren(Long parentId) {
		List<ChildrenListResponse.ChildItem> items = childrenRepository.findAllByParentIdAndDeletedAtIsNull(parentId)
			.stream()
			.map(child -> {
				Long accountId = accountRepository.findByChildId(child.getId())
					.map(account -> account.getAccountId())
					.orElse(null);
				return ChildrenListResponse.ChildItem.from(child, accountId);
			})
			.toList();
		return new ChildrenListResponse(items);
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
	public void connectAllowance(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		if (child.isAllowanceLinked()) {
			throw new GeneralException(ErrorStatus.CHILDREN_ALLOWANCE_ALREADY_LINKED);
		}

		child.linkAllowance();
	}

	@Transactional
	public void disconnectAllowance(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		if (!child.isAllowanceLinked()) {
			throw new GeneralException(ErrorStatus.CHILDREN_ALLOWANCE_NOT_LINKED);
		}

		child.unlinkAllowance();
	}

	public AllowanceStatusResponse getAllowanceStatus(Long parentId, Long childId) {
		Children child = childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		return new AllowanceStatusResponse(child.isAllowanceLinked());
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

	public ChildHoldingsResponse getHoldings(Long parentId, Long childId) {
		childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
				.orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

		Long accountId = accountRepository.findByChildId(childId)
				.map(account -> account.getAccountId())
				.orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

		List<ChildHoldingsResponse.HoldingDto> holdings = accountEtfHoldingRepository
				.findByIdAccountId(accountId)
				.stream()
				.map(h -> ChildHoldingsResponse.HoldingDto.builder()
						.etfId(h.getId().getEtfId())
						.qty(h.getQty())
						.avgBuyPrice(h.getAvgBuyPrice())
						.build())
				.toList();

		return ChildHoldingsResponse.builder()
				.holdings(holdings)
				.build();
	}
}