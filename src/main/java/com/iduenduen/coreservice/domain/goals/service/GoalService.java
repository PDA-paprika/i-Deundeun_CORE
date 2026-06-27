package com.iduenduen.coreservice.domain.goals.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import com.iduenduen.coreservice.domain.goals.dto.GoalCreateRequest;
import com.iduenduen.coreservice.domain.goals.dto.GoalCreateResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalDetailResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalListResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalPreviewResponse;
import com.iduenduen.coreservice.domain.goals.dto.GoalUpdateRequest;
import com.iduenduen.coreservice.domain.goals.dto.GoalUpdateResponse;
import com.iduenduen.coreservice.domain.goals.entity.Goal;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

    private final GoalRepository goalRepository;
    private final ChildrenRepository childrenRepository;
    private final ExecutionGoalLinkRepository executionGoalLinkRepository;

    public GoalListResponse getGoals(Long parentId, Long childId, GoalStatus status) {
        validateChildOwnership(parentId, childId);
        List<Goal> goals = goalRepository.findAllByChildIdAndParentIdAndStatusAndDeletedAtIsNull(
            childId, parentId, status);

        List<Long> goalIds = goals.stream().map(Goal::getId).toList();
        Map<Long, Long> investedAmtMap = executionGoalLinkRepository.sumInvestedAmtByGoalIds(goalIds)
            .stream()
            .collect(Collectors.toMap(
                row -> ((Number) row[0]).longValue(),
                row -> ((Number) row[1]).longValue()
            ));

        return GoalListResponse.fromWithRate(goals, investedAmtMap);
    }

    public GoalDetailResponse getGoal(Long parentId, Long childId, Long goalId) {
        Goal goal = findGoal(parentId, childId, goalId);
        return GoalDetailResponse.from(goal);
    }

    @Transactional
    public GoalCreateResponse createGoal(Long parentId, Long childId, GoalCreateRequest request) {
        validateChildOwnership(parentId, childId);

        Goal goal = Goal.builder()
            .parentId(parentId)
            .childId(childId)
            .goalType1(request.goalType1())
            .goalType2(request.goalType2())
            .goalType3(request.goalType3() != null ? request.goalType3() : 0)
            .name(request.name())
            .targetAmount(request.targetAmount())
            .targetDate(request.targetDate())
            .build();

        return GoalCreateResponse.from(goalRepository.save(goal));
    }

    @Transactional
    public GoalUpdateResponse updateGoal(Long parentId, Long childId, Long goalId, GoalUpdateRequest request) {
        Goal goal = findGoal(parentId, childId, goalId);

        goal.update(
            request.name(),
            request.targetAmount(),
            request.targetDate(),
            request.goalType1(),
            request.goalType2(),
            request.goalType3()
        );

        Long investedAmt = executionGoalLinkRepository.sumInvestedAmtByGoalIds(List.of(goal.getId()))
            .stream().findFirst().map(row -> ((Number) row[1]).longValue()).orElse(0L);
        BigDecimal achievedPct = calculateRate(investedAmt, goal.getTargetAmount());
        goal.updateAchievedPct(achievedPct);

        return new GoalUpdateResponse(goal.getId(), achievedPct);
    }

    @Transactional
    public void deleteGoal(Long parentId, Long childId, Long goalId) {
        Goal goal = findGoal(parentId, childId, goalId);
        goal.softDelete();
    }

    public GoalPreviewResponse previewGoal(Long parentId, Long childId, Long goalId,
                                           Long targetAmount, LocalDate targetDate) {
        Goal goal = findGoal(parentId, childId, goalId);

        // TODO: ETF 서버 연동 후 실제 값 추가 필요
        BigDecimal currentRate = goal.getAchievedPct();
        BigDecimal previewRate = calculateRate(0L, targetAmount != null ? targetAmount : goal.getTargetAmount());

        return new GoalPreviewResponse(currentRate, previewRate);
    }

    // 스케줄러 용 FAILED 처리 메소드
    @Transactional
    public void expireOverdueGoals() {
        List<Goal> overdueGoals = goalRepository.findAllByStatusAndTargetDateBefore(
            GoalStatus.ACTIVE, LocalDate.now());
        overdueGoals.forEach(Goal::expire);
    }

    private BigDecimal calculateRate(Long currentAmount, Long targetAmount) {
        if (targetAmount == null || targetAmount <= 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(currentAmount)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(targetAmount), 2, RoundingMode.HALF_UP);
    }

    private void validateChildOwnership(Long parentId, Long childId) {
        childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(childId, parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));
    }

    private Goal findGoal(Long parentId, Long childId, Long goalId) {
        return goalRepository.findByIdAndChildIdAndParentIdAndDeletedAtIsNull(goalId, childId, parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.GOAL_NOT_FOUND));
    }
}
