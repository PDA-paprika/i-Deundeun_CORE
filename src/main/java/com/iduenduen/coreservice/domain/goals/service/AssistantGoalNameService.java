package com.iduenduen.coreservice.domain.goals.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameCreateRequest;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameCreateResponse;
import com.iduenduen.coreservice.domain.goals.dto.AssistantGoalNameGetResponse;
import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalName;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;
import com.iduenduen.coreservice.domain.goals.repository.AssistantGoalNameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssistantGoalNameService {

    private final AssistantGoalNameRepository assistantGoalNameRepository;

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

        AssistantGoalName entity = AssistantGoalName.builder()
            .parentId(parentId)
            .goalType1(goalType1)
            .goalType2(request.goalType2())
            .goalType3(request.goalType3() != null ? request.goalType3() : 0)
            .name(request.name())
            .build();

        return AssistantGoalNameCreateResponse.from(assistantGoalNameRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AssistantGoalNameGetResponse> getList(Long parentId) {
        return assistantGoalNameRepository.findByParentIdAndDeletedAtIsNull(parentId)
            .stream()
            .map(AssistantGoalNameGetResponse::from)
            .collect(Collectors.toList());
    }
}
