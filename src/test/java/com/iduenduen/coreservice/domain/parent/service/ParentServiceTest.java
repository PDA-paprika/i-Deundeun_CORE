package com.iduenduen.coreservice.domain.parent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountCashHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftContractRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftTransferRepository;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;
import com.iduenduen.coreservice.domain.onboarding.repository.UserAgreementRepository;
import com.iduenduen.coreservice.domain.parent.dto.ParentUpdateRequest;
import com.iduenduen.coreservice.domain.parent.dto.SelectedChildRequest;
import com.iduenduen.coreservice.domain.parent.dto.WizardProfileRequest;
import com.iduenduen.coreservice.domain.parent.entity.Parent;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock private ParentRepository parentRepository;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private AccountRepository accountRepository;
    @Mock private AccountEtfHoldingRepository accountEtfHoldingRepository;
    @Mock private AccountCashHistoryRepository accountCashHistoryRepository;
    @Mock private AccountEtfHistoryRepository accountEtfHistoryRepository;
    @Mock private ChildrenRepository childrenRepository;
    @Mock private GoalRepository goalRepository;
    @Mock private GiftContractRepository giftContractRepository;
    @Mock private GiftTransferRepository giftTransferRepository;
    @Mock private UserAgreementRepository userAgreementRepository;
    @Mock private ExecutionGoalLinkRepository executionGoalLinkRepository;

    @InjectMocks
    private ParentService parentService;

    private Parent createParent() {
        Parent parent = Parent.builder()
                .email("test@example.com")
                .accountNumber("1234567890")
                .passwordHash("hashed")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .relation("MOTHER")
                .region("서울")
                .childCount(1)
                .certFileUrl("https://example.com/cert.pdf")
                .build();
        ReflectionTestUtils.setField(parent, "id", 1L);
        return parent;
    }

    @Test
    void getMe_성공() {
        Parent parent = createParent();
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));
        given(accountRepository.findByParentId(1L)).willReturn(Optional.empty());

        var response = parentService.getMe(1L);

        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getRegion()).isEqualTo("서울");
    }

    @Test
    void getMe_존재하지_않으면_예외() {
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.getMe(1L))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void updateMe_성공() {
        Parent parent = createParent();
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));

        ParentUpdateRequest request = new ParentUpdateRequest();
        ReflectionTestUtils.setField(request, "name", "김철수");
        ReflectionTestUtils.setField(request, "birthDate", LocalDate.of(1995, 5, 5));
        ReflectionTestUtils.setField(request, "relation", "FATHER");
        ReflectionTestUtils.setField(request, "childCount", 2);
        ReflectionTestUtils.setField(request, "region", "부산");
        ReflectionTestUtils.setField(request, "profileImageUrl", "https://example.com/profile.png");

        var response = parentService.updateMe(1L, request);

        assertThat(response.getParentId()).isEqualTo(parent.getId());
        assertThat(parent.getName()).isEqualTo("김철수");
        assertThat(parent.getRegion()).isEqualTo("부산");
    }

    @Test
    void updateSelectedChild_성공() {
        Parent parent = createParent();
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));

        SelectedChildRequest request = new SelectedChildRequest();
        ReflectionTestUtils.setField(request, "childId", 1L);

        var response = parentService.updateSelectedChild(1L, request);

        assertThat(response.getSelectedChildId()).isEqualTo(1L);
    }

    @Test
    void updateWizardProfile_성공() {
        Parent parent = createParent();
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));

        WizardProfileRequest request = new WizardProfileRequest();
        ReflectionTestUtils.setField(request, "monthlyHouseholdIncome", 3);
        ReflectionTestUtils.setField(request, "parentEconomicActivity", 1);
        ReflectionTestUtils.setField(request, "educationLevel", 4);

        parentService.updateWizardProfile(1L, request);

        assertThat(parent.getMonthlyHouseholdIncome()).isEqualTo(3);
        assertThat(parent.getParentEconomicActivity()).isEqualTo(1);
        assertThat(parent.getEducationLevel()).isEqualTo(4);
    }

    @Test
    void updateMe_존재하지_않으면_예외() {
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.updateMe(1L, new ParentUpdateRequest()))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void updateSelectedChild_존재하지_않으면_예외() {
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.updateSelectedChild(1L, new SelectedChildRequest()))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void updateWizardProfile_존재하지_않으면_예외() {
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.updateWizardProfile(1L, new WizardProfileRequest()))
                .isInstanceOf(GeneralException.class);
    }

    @Test
    void withdraw_성공() {
        Parent parent = createParent();
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));
        given(executionGoalLinkRepository.findAllByParentId(1L)).willReturn(List.of());
        given(accountRepository.findByParentId(1L)).willReturn(Optional.empty());
        given(giftContractRepository.findAllByParentId(1L)).willReturn(List.of());
        given(goalRepository.findAllByParentId(1L)).willReturn(List.of());
        given(childrenRepository.findAllByParentId(1L)).willReturn(List.of());
        given(userAgreementRepository.findAllByParent_Id(1L)).willReturn(List.of());

        parentService.withdraw(1L);

        assertThat(parent.getDeletedAt()).isNotNull();
    }

    @Test
    void withdraw_존재하지_않으면_예외() {
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.withdraw(1L))
                .isInstanceOf(GeneralException.class);
    }
}
