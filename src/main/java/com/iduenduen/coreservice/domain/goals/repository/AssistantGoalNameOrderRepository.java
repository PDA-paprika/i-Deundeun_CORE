package com.iduenduen.coreservice.domain.goals.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iduenduen.coreservice.domain.goals.entity.AssistantGoalNameOrder;

public interface AssistantGoalNameOrderRepository extends JpaRepository<AssistantGoalNameOrder, Long> {

    @Query("SELECT o FROM AssistantGoalNameOrder o JOIN FETCH o.goal WHERE o.parentId = :parentId ORDER BY o.sortOrder ASC")
    List<AssistantGoalNameOrder> findByParentIdOrderBySortOrder(@Param("parentId") Long parentId);

    @Query("SELECT o FROM AssistantGoalNameOrder o JOIN FETCH o.goal WHERE o.parentId = :parentId AND o.goal.id IN :goalIds")
    List<AssistantGoalNameOrder> findByParentIdAndGoalIds(@Param("parentId") Long parentId, @Param("goalIds") List<Long> goalIds);

    @Query("SELECT COALESCE(MAX(o.sortOrder), 0) FROM AssistantGoalNameOrder o WHERE o.parentId = :parentId")
    Integer findMaxSortOrderByParentId(@Param("parentId") Long parentId);
}
