package com.iduenduen.coreservice.domain.parent.service;

import java.util.List;

import com.iduenduen.coreservice.domain.account.entity.AccountEtfHolding;
import com.iduenduen.coreservice.domain.account.repository.AccountCashHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.repository.GiftContractRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftTransferRepository;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;
import com.iduenduen.coreservice.domain.onboarding.repository.UserAgreementRepository;
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
    private final AccountEtfHoldingRepository accountEtfHoldingRepository;
    private final AccountCashHistoryRepository accountCashHistoryRepository;
    private final AccountEtfHistoryRepository accountEtfHistoryRepository;
    private final ChildrenRepository childrenRepository;
    private final GoalRepository goalRepository;
    private final GiftContractRepository giftContractRepository;
    private final GiftTransferRepository giftTransferRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final ExecutionGoalLinkRepository executionGoalLinkRepository;

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
                .monthlyHouseholdIncome(parent.getMonthlyHouseholdIncome())
                .parentEconomicActivity(parent.getParentEconomicActivity())
                .educationLevel(parent.getEducationLevel())
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
                request.getMonthlyHouseholdIncome(),
                request.getParentEconomicActivity(),
                request.getEducationLevel()
        );

    }

    @Transactional
    public void updateCertFileUrl(Long parentId, String certFileUrl) {
        Parent parent = findActiveParent(parentId);
        parent.updateCertFileUrl(certFileUrl);
    }

    @Transactional
    public void withdraw(Long parentId) {
        // 1. execution_goal_links → hard delete (FK: parent, child, goal, etf_history)
        executionGoalLinkRepository.deleteAll(
                executionGoalLinkRepository.findAllByParentId(parentId));

        // 2. account 관련 이력 + 잔고 → hard delete
        accountRepository.findByParentId(parentId).ifPresent(account -> {
            Long accountId = account.getAccountId();
            accountEtfHistoryRepository.deleteAll(
                    accountEtfHistoryRepository.findByAccountIdOrderByOccurredAtDesc(accountId));
            accountCashHistoryRepository.deleteAll(
                    accountCashHistoryRepository.findByAccountIdOrderByOccurredAtDesc(accountId));
            accountEtfHoldingRepository.deleteAll(
                    accountEtfHoldingRepository.findByIdAccountId(accountId));
            accountRepository.delete(account);
        });

        // 3. gift_transfers → hard delete (FK: gift_contracts)
        List<GiftContract> contracts = giftContractRepository.findAllByParentId(parentId);
        if (!contracts.isEmpty()) {
            List<Long> contractIds = contracts.stream().map(GiftContract::getId).toList();
            List<GiftTransfer> transfers = giftTransferRepository.findAllByGiftContractIdIn(contractIds);
            giftTransferRepository.deleteAll(transfers);
            giftContractRepository.deleteAll(contracts);
        }

        // 4. goals → hard delete
        goalRepository.deleteAll(goalRepository.findAllByParentId(parentId));

        // 5. children → hard delete
        childrenRepository.deleteAll(childrenRepository.findAllByParentId(parentId));

        // 6. user_agreements → hard delete
        userAgreementRepository.deleteAll(userAgreementRepository.findAllByParent_Id(parentId));

        // 7. parent → soft delete
        Parent parent = findActiveParent(parentId);
        parent.withdraw();
    }

    private Parent findActiveParent(Long parentId) {
        return parentRepository.findByIdAndDeletedAtIsNull(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
    }
}
