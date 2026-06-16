package com.iduenduen.coreservice.domain.parent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.domain.parent.dto.ParentUpdateRequest;
import com.iduenduen.coreservice.domain.parent.dto.SelectedChildRequest;
import com.iduenduen.coreservice.domain.parent.dto.WizardProfileRequest;
import com.iduenduen.coreservice.domain.parent.entity.Parent;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;

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
        ReflectionTestUtils.setField(request, "childId", "child-1");

        var response = parentService.updateSelectedChild(1L, request);

        assertThat(response.getSelectedChildId()).isEqualTo("child-1");
    }

    @Test
    void updateWizardProfile_성공() {
        Parent parent = createParent();
        given(parentRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(parent));

        WizardProfileRequest request = new WizardProfileRequest();
        ReflectionTestUtils.setField(request, "incomeLevel", "MID");
        ReflectionTestUtils.setField(request, "assetRange", "1-3억");
        ReflectionTestUtils.setField(request, "educationHeat", 4);
        ReflectionTestUtils.setField(request, "dualIncome", true);

        parentService.updateWizardProfile(1L, request);

        assertThat(parent.getIncomeLevel()).isEqualTo("MID");
        assertThat(parent.getEducationHeat()).isEqualTo(4);
        assertThat(parent.getDualIncome()).isTrue();
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
