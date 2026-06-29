package com.iduenduen.coreservice.domain.goals.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.domain.children.entity.Children;
import com.iduenduen.coreservice.domain.children.enums.CreatedVia;
import com.iduenduen.coreservice.domain.children.enums.Gender;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.goals.dto.GoalCreateRequest;
import com.iduenduen.coreservice.domain.goals.dto.GoalUpdateRequest;
import com.iduenduen.coreservice.domain.goals.entity.Goal;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private ChildrenRepository childrenRepository;

    @InjectMocks
    private GoalService goalService;

    private static final Long PARENT_ID = 1L;
    private static final Long CHILD_ID = 2L;
    private static final Long GOAL_ID = 3L;

    private Children child;
    private Goal goal;

    @BeforeEach
    void setUp() {
        child = Children.builder()
            .parentId(PARENT_ID)
            .name("홍길동")
            .birthDate(LocalDate.of(2018, 3, 15))
            .gender(Gender.MALE)
            .birthOrder(1)
            .securitiesAccount("1234567890")
            .createdVia(CreatedVia.ONBOARDING)
            .build();
        ReflectionTestUtils.setField(child, "id", CHILD_ID);

        goal = Goal.builder()
            .parentId(PARENT_ID)
            .childId(CHILD_ID)
            .goalType1(GoalType.EDUCATION)
            .name("대학 등록금")
            .targetAmount(50_000_000L)
            .targetDate(LocalDate.of(2036, 3, 1))
            .build();
        ReflectionTestUtils.setField(goal, "id", GOAL_ID);
    }

    // ── 목표 목록 조회 ──────────────────────────────────────────

    @Test
    void getGoals_성공() {
        given(childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(child));
        given(goalRepository.findAllByChildIdAndParentIdAndStatus(
            CHILD_ID, PARENT_ID, GoalStatus.ACTIVE))
            .willReturn(List.of(goal));

        var response = goalService.getGoals(PARENT_ID, CHILD_ID, GoalStatus.ACTIVE);

        assertThat(response.goals()).hasSize(1);
        assertThat(response.goals().get(0).name()).isEqualTo("대학 등록금");
    }

    @Test
    void getGoals_타인_자녀면_예외() {
        given(childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(CHILD_ID, PARENT_ID))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoals(PARENT_ID, CHILD_ID, GoalStatus.ACTIVE))
            .isInstanceOf(GeneralException.class);
    }

    // ── 목표 단건 조회 ──────────────────────────────────────────

    @Test
    void getGoal_성공() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(goal));

        var response = goalService.getGoal(PARENT_ID, CHILD_ID, GOAL_ID);

        assertThat(response.goalId()).isEqualTo(GOAL_ID);
        assertThat(response.goalType1()).isEqualTo(GoalType.EDUCATION);
        assertThat(response.targetAmount()).isEqualTo(50_000_000L);
    }

    @Test
    void getGoal_존재하지_않으면_예외() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoal(PARENT_ID, CHILD_ID, GOAL_ID))
            .isInstanceOf(GeneralException.class);
    }

    // ── 목표 생성 ───────────────────────────────────────────────

    @Test
    void createGoal_성공() {
        given(childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(child));
        given(goalRepository.save(any(Goal.class))).willReturn(goal);

        GoalCreateRequest request = new GoalCreateRequest(
            GoalType.EDUCATION, null, null,
            "대학 등록금", 50_000_000L, LocalDate.of(2036, 3, 1)
        );

        var response = goalService.createGoal(PARENT_ID, CHILD_ID, request);

        assertThat(response.goalId()).isEqualTo(GOAL_ID);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void createGoal_타인_자녀면_예외() {
        given(childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(CHILD_ID, PARENT_ID))
            .willReturn(Optional.empty());

        GoalCreateRequest request = new GoalCreateRequest(
            GoalType.EDUCATION, null, null,
            "대학 등록금", 50_000_000L, LocalDate.of(2036, 3, 1)
        );

        assertThatThrownBy(() -> goalService.createGoal(PARENT_ID, CHILD_ID, request))
            .isInstanceOf(GeneralException.class);
    }

    // ── 목표 수정 ───────────────────────────────────────────────

    @Test
    void updateGoal_성공() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(goal));

        GoalUpdateRequest request = new GoalUpdateRequest(
            null, null, null,
            "결혼 자금", 70_000_000L, LocalDate.of(2040, 1, 1)
        );

        var response = goalService.updateGoal(PARENT_ID, CHILD_ID, GOAL_ID, request);

        assertThat(response.goalId()).isEqualTo(GOAL_ID);
        assertThat(goal.getName()).isEqualTo("결혼 자금");
        assertThat(goal.getTargetAmount()).isEqualTo(70_000_000L);
    }

    @Test
    void updateGoal_존재하지_않으면_예외() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.empty());

        GoalUpdateRequest request = new GoalUpdateRequest(
            null, null, null, "결혼 자금", 70_000_000L, LocalDate.of(2040, 1, 1)
        );

        assertThatThrownBy(() -> goalService.updateGoal(PARENT_ID, CHILD_ID, GOAL_ID, request))
            .isInstanceOf(GeneralException.class);
    }

    // ── 목표 삭제 ───────────────────────────────────────────────

    @Test
    void deleteGoal_성공() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(goal));

        goalService.deleteGoal(PARENT_ID, CHILD_ID, GOAL_ID);

        verify(goalRepository).delete(goal);
    }

    @Test
    void deleteGoal_존재하지_않으면_예외() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.deleteGoal(PARENT_ID, CHILD_ID, GOAL_ID))
            .isInstanceOf(GeneralException.class);
    }

    // ── 달성률 미리보기 ─────────────────────────────────────────

    @Test
    void previewGoal_목표금액_변경시_미리보기() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(goal));

        var response = goalService.previewGoal(PARENT_ID, CHILD_ID, GOAL_ID, 100_000_000L, null);

        // ETF 연동 전이라 currentAmount=0이므로 둘 다 0
        assertThat(response.currentAchievementRate()).isEqualByComparingTo("0.00");
        assertThat(response.previewAchievementRate()).isEqualByComparingTo("0.00");
    }

    @Test
    void previewGoal_파라미터_없으면_기존_목표금액으로_계산() {
        given(goalRepository.findByIdAndChildIdAndParentId(GOAL_ID, CHILD_ID, PARENT_ID))
            .willReturn(Optional.of(goal));

        var response = goalService.previewGoal(PARENT_ID, CHILD_ID, GOAL_ID, null, null);

        assertThat(response.currentAchievementRate()).isEqualByComparingTo("0.00");
        assertThat(response.previewAchievementRate()).isEqualByComparingTo("0.00");
    }

    // ── 만료 처리 ───────────────────────────────────────────────

    @Test
    void expireOverdueGoals_성공() {
        given(goalRepository.findAllByStatusAndTargetDateBefore(any(), any()))
            .willReturn(List.of(goal));

        goalService.expireOverdueGoals();

        assertThat(goal.getStatus()).isEqualTo(GoalStatus.FAILED);
    }
}
