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
import com.iduenduen.coreservice.domain.goals.entity.GoalOrder;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.repository.GoalOrderRepository;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalOrderRepository goalOrderRepository;
    private final ChildrenRepository childrenRepository;
    private final ExecutionGoalLinkRepository executionGoalLinkRepository;

    public GoalListResponse getGoals(Long parentId, Long childId, GoalStatus status) {
        validateChildOwnership(parentId, childId);
        List<Goal> goals = goalRepository.findAllByChildIdAndParentIdAndStatus(childId, parentId, status);

        List<GoalOrder> orders = goalOrderRepository.findByChildIdOrderBySortOrder(childId);
        Map<Long, Integer> orderMap = orders.stream()
            .collect(Collectors.toMap(o -> o.getGoal().getId(), GoalOrder::getSortOrder,
                (a, b) -> a));

        goals = goals.stream()
            .sorted((a, b) -> {
                Integer oa = orderMap.get(a.getId());
                Integer ob = orderMap.get(b.getId());
                if (oa != null && ob != null) return oa.compareTo(ob);
                if (oa != null) return -1;
                if (ob != null) return 1;
                return a.getCreatedAt().compareTo(b.getCreatedAt());
            })
            .collect(Collectors.toList());

        List<Long> goalIds = goals.stream().map(Goal::getId).toList();
        Map<Long, Long> investedAmtMap = goalIds.isEmpty() ? Map.of() :
            executionGoalLinkRepository.sumInvestedAmtByGoalIds(goalIds)
                .stream()
                .collect(Collectors.toMap(
                    row -> ((Number) row[0]).longValue(),
                    row -> ((Number) row[1]).longValue()
                ));

        return GoalListResponse.fromWithRate(goals, investedAmtMap);
    }

    public GoalDetailResponse getGoal(Long parentId, Long childId, Long goalId) {
        Goal goal = findGoal(parentId, childId, goalId);
        Long investedAmt = executionGoalLinkRepository.sumInvestedAmtByGoalIds(List.of(goal.getId()))
            .stream().findFirst().map(row -> ((Number) row[1]).longValue()).orElse(0L);
        BigDecimal achievedPct = BigDecimal.valueOf(investedAmt)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(goal.getTargetAmount() > 0 ? goal.getTargetAmount() : 1), 2, RoundingMode.HALF_UP)
            .min(BigDecimal.valueOf(100));
        return GoalDetailResponse.from(goal, achievedPct);
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
            .level(0)
            .build();

        Goal saved = goalRepository.save(goal);
        int nextOrder = goalOrderRepository.findMaxSortOrderByChildId(childId) + 1;
        goalOrderRepository.save(GoalOrder.builder()
            .childId(childId)
            .goal(saved)
            .sortOrder(nextOrder)
            .build());
        return GoalCreateResponse.from(saved);
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
        executionGoalLinkRepository.unlinkByGoalId(goalId);
        goalRepository.delete(goal);
    }

    @Transactional
    public void reorder(Long parentId, Long childId, List<Long> ids) {
        validateChildOwnership(parentId, childId);
        List<Goal> goals = goalRepository.findAllById(ids);
        boolean allOwned = goals.stream().allMatch(g -> g.getChildId().equals(childId) && g.getParentId().equals(parentId));
        if (goals.size() != ids.size() || !allOwned) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Map<Long, GoalOrder> orderMap = goalOrderRepository.findByChildIdAndGoalIds(childId, ids)
            .stream()
            .collect(Collectors.toMap(o -> o.getGoal().getId(), o -> o, (a, b) ->
                a.getCreatedAt().isBefore(b.getCreatedAt()) ? a : b));

        Map<Long, Goal> goalMap = goals.stream()
            .collect(Collectors.toMap(Goal::getId, g -> g));

        for (int i = 0; i < ids.size(); i++) {
            Long goalId = ids.get(i);
            int sortOrder = i + 1;
            GoalOrder existing = orderMap.get(goalId);
            if (existing != null) {
                existing.updateSortOrder(sortOrder);
            } else {
                goalOrderRepository.save(GoalOrder.builder()
                    .childId(childId)
                    .goal(goalMap.get(goalId))
                    .sortOrder(sortOrder)
                    .build());
            }
        }
    }

    public GoalPreviewResponse previewGoal(Long parentId, Long childId, Long goalId,
                                           Long targetAmount, LocalDate targetDate) {
        Goal goal = findGoal(parentId, childId, goalId);

        // TODO: ETF 서버 연동 후 실제 값 추가 필요
        BigDecimal currentRate = goal.getAchievedPct();
        BigDecimal previewRate = calculateRate(0L, targetAmount != null ? targetAmount : goal.getTargetAmount());

        return new GoalPreviewResponse(currentRate, previewRate);
    }

    @Transactional
    public void updateLevel(Long parentId, Long childId, Long goalId, int level) {
        Goal goal = findGoal(parentId, childId, goalId);
        goal.updateLevel(level);
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
        return goalRepository.findByIdAndChildIdAndParentId(goalId, childId, parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.GOAL_NOT_FOUND));
    }
}
