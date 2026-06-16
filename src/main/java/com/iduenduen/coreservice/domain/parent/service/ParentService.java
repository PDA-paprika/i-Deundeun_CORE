package com.iduenduen.coreservice.domain.parent.service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    private final ParentRepository parentRepository;

    public ParentResponse getMe(String parentId) {
        Parent parent = findActiveParent(parentId);

        return ParentResponse.builder()
                .id(parent.getId())
                .name(parent.getName())
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
    public ParentUpdateResponse updateMe(String parentId, ParentUpdateRequest request) {
        Parent parent = findActiveParent(parentId);

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
    public SelectedChildResponse updateSelectedChild(String parentId, SelectedChildRequest request) {
        Parent parent = findActiveParent(parentId);

        parent.updateSelectedChild(request.getChildId());

        return SelectedChildResponse.builder()
                .selectedChildId(parent.getSelectedChildId())
                .build();
    }

    @Transactional
    public void updateWizardProfile(String parentId, WizardProfileRequest request) {
        Parent parent = findActiveParent(parentId);

        parent.updateWizardProfile(
                request.getIncomeLevel(),
                request.getAssetRange(),
                request.getEducationHeat(),
                request.getDualIncome()
        );
    }

    private Parent findActiveParent(String parentId) {
        return parentRepository.findByIdAndDeletedAtIsNull(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
    }
}
