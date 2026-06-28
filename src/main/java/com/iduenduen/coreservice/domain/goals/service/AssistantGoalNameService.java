package com.iduenduen.coreservice.domain.goals.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameCreateRequest;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameCreateResponse;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameGetResponse;
import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalName;
import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalNameOrder;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;
import com.iduenduen.coreservice.domain.goals.repository.AssistantGoalNameOrderRepository;
import com.iduenduen.coreservice.domain.goals.repository.AssistantGoalNameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssistantGoalNameService {

    private final AssistantGoalNameRepository assistantGoalNameRepository;
    private final AssistantGoalNameOrderRepository assistantGoalNameOrderRepository;

    private static final java.util.Set<Integer> ALLOWED_GOAL_TYPE1 = java.util.Set.of(1, 2);

    @Transactional
    public AssistantGoalNameCreateResponse create(Long parentId, AssistantGoalNameCreateRequest request) {
        if (!ALLOWED_GOAL_TYPE1.contains(request.goalType1())) {
            throw new GeneralException(ErrorStatus.ASSISTANT_GOAL_NAME_INVALID_TYPE);
        }
        GoalType goalType1 = Arrays.stream(GoalType.values())
            .filter(t -> t.getCode() == request.goalType1())
            .findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus.ASSISTANT_GOAL_NAME_INVALID_TYPE));

        Integer goalType3 = request.goalType3() != null ? request.goalType3() : 0;
        if (assistantGoalNameRepository.existsByParentIdAndGoalType1AndGoalType2AndGoalType3(parentId, goalType1, request.goalType2(), goalType3)) {
            throw new GeneralException(ErrorStatus.ASSISTANT_GOAL_NAME_DUPLICATE);
        }

        AssistantGoalName entity = AssistantGoalName.builder()
            .parentId(parentId)
            .goalType1(goalType1)
            .goalType2(request.goalType2())
            .goalType3(goalType3)
            .name(request.name())
            .build();
        assistantGoalNameRepository.save(entity);

        int nextOrder = assistantGoalNameOrderRepository.findMaxSortOrderByParentId(parentId) + 1;
        assistantGoalNameOrderRepository.save(AssistantGoalNameOrder.builder()
            .parentId(parentId)
            .goal(entity)
            .sortOrder(nextOrder)
            .build());

        return AssistantGoalNameCreateResponse.from(entity);
    }

    @Transactional
    public void delete(Long parentId, Long id) {
        AssistantGoalName entity = assistantGoalNameRepository.findByIdAndParentId(id, parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
        assistantGoalNameRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<AssistantGoalNameGetResponse> getList(Long parentId) {
        List<AssistantGoalName> all = assistantGoalNameRepository.findByParentId(parentId);
        List<AssistantGoalNameOrder> orders = assistantGoalNameOrderRepository.findByParentIdOrderBySortOrder(parentId);

        Map<Long, Integer> orderMap = orders.stream()
            .collect(Collectors.toMap(o -> o.getGoal().getId(), AssistantGoalNameOrder::getSortOrder,
                (a, b) -> a));

        return all.stream()
            .sorted((a, b) -> {
                Integer oa = orderMap.get(a.getId());
                Integer ob = orderMap.get(b.getId());
                if (oa != null && ob != null) return oa.compareTo(ob);
                if (oa != null) return -1;
                if (ob != null) return 1;
                return a.getCreatedAt().compareTo(b.getCreatedAt());
            })
            .map(AssistantGoalNameGetResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void reorder(Long parentId, List<Long> ids) {
        List<AssistantGoalName> goals = assistantGoalNameRepository.findAllById(ids);
        boolean allOwned = goals.stream().allMatch(g -> g.getParentId().equals(parentId));
        if (goals.size() != ids.size() || !allOwned) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Map<Long, AssistantGoalNameOrder> orderMap = assistantGoalNameOrderRepository
            .findByParentIdAndGoalIds(parentId, ids)
            .stream()
            .collect(Collectors.toMap(o -> o.getGoal().getId(), o -> o,
                (a, b) -> a.getCreatedAt().isBefore(b.getCreatedAt()) ? a : b));

        Map<Long, AssistantGoalName> goalMap = goals.stream()
            .collect(Collectors.toMap(AssistantGoalName::getId, g -> g));

        for (int i = 0; i < ids.size(); i++) {
            Long goalId = ids.get(i);
            int sortOrder = i + 1;
            AssistantGoalNameOrder existing = orderMap.get(goalId);
            if (existing != null) {
                existing.updateSortOrder(sortOrder);
            } else {
                assistantGoalNameOrderRepository.save(AssistantGoalNameOrder.builder()
                    .parentId(parentId)
                    .goal(goalMap.get(goalId))
                    .sortOrder(sortOrder)
                    .build());
            }
        }
    }
}
