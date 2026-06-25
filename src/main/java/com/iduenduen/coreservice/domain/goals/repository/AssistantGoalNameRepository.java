package com.iduenduen.coreservice.domain.goals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalName;

public interface AssistantGoalNameRepository extends JpaRepository<AssistantGoalName, Long> {

    List<AssistantGoalName> findByParentIdAndDeletedAtIsNull(Long parentId);

    java.util.Optional<AssistantGoalName> findByIdAndParentIdAndDeletedAtIsNull(Long id, Long parentId);
}
