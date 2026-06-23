package com.iduenduen.coreservice.domain.parent.service;

import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.parent.dto.ParentResponse;
import com.iduenduen.coreservice.domain.parent.dto.ParentUpdateRequest;
import com.iduenduen.coreservice.domain.parent.dto.ParentUpdateResponse;
import com.iduenduen.coreservice.domain.parent.dto.SelectedChildRequest;
import com.iduenduen.coreservice.domain.parent.dto.SelectedChildResponse;
import com.iduenduen.coreservice.domain.parent.dto.WizardProfileRequest;
import com.iduenduen.coreservice.domain.parent.entity.Parent;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;
import com.iduenduen.coreservice.domain.account.entity.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    private final ParentRepository parentRepository;
    private final AccountRepository accountRepository;

    public ParentResponse getMe(Long parentId) {
        Parent parent = findActiveParent(parentId);
        Long accountId = accountRepository.findByParentId(parentId)
                .map(Account::getAccountId)
                .orElse(null);

        return ParentResponse.builder()
                .id(parent.getId())
                .accountId(accountId)
                .name(parent.getName())
                .accountNumber(parent.getAccountNumber())
                .birthDate(parent.getBirthDate())
                .relation(parent.getRelation())
                .region(parent.getRegion())
                .childCount(parent.getChildCount())
                .profileImageUrl(parent.getProfileImageUrl())
                .incomeLevel(parent.getIncomeLevel())
                .assetRange(parent.getAssetRange())
                .educationHeat(parent.getEducationHeat())
                .dualIncome(parent.getDualIncome())
                .clusterValue(parent.getClusterValue())
                .selectedChildId(parent.getSelectedChildId())
                .build();
    }

    @Transactional
    public ParentUpdateResponse updateMe(Long parentId, ParentUpdateRequest request) {
        Parent parent = findActiveParent(parentId);

        if (request.getName() == null || request.getBirthDate() == null || request.getRelation() == null
                || request.getChildCount() == null || request.getRegion() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        parent.updateProfile(
                request.getName(),
                request.getBirthDate(),
                request.getRelation(),
                request.getChildCount(),
                request.getRegion(),
                request.getProfileImageUrl()
        );

        return ParentUpdateResponse.builder()
                .parentId(parent.getId())
                .build();
    }

    @Transactional
    public SelectedChildResponse updateSelectedChild(Long parentId, SelectedChildRequest request) {
        Parent parent = findActiveParent(parentId);

        parent.updateSelectedChild(request.getChildId());

        return SelectedChildResponse.builder()
                .selectedChildId(parent.getSelectedChildId())
                .build();
    }

    @Transactional
    public void updateWizardProfile(Long parentId, WizardProfileRequest request) {
        Parent parent = findActiveParent(parentId);

        parent.updateWizardProfile(
                request.getIncomeLevel(),
                request.getAssetRange(),
                request.getEducationHeat(),
                request.getDualIncome()
        );
    }

    @Transactional
    public void updateCertFileUrl(Long parentId, String certFileUrl) {
        Parent parent = findActiveParent(parentId);
        parent.updateCertFileUrl(certFileUrl);
    }

    @Transactional
    public void withdraw(Long parentId) {
        Parent parent = findActiveParent(parentId);
        parent.withdraw();
    }

    private Parent findActiveParent(Long parentId) {
        return parentRepository.findByIdAndDeletedAtIsNull(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
    }
}
