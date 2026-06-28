package com.iduenduen.coreservice.domain.goals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iduenduen.coreservice.domain.goals.entity.GoalOrder;

public interface GoalOrderRepository extends JpaRepository<GoalOrder, Long> {

    @Query("SELECT o FROM GoalOrder o JOIN FETCH o.goal WHERE o.childId = :childId ORDER BY o.sortOrder ASC")
    List<GoalOrder> findByChildIdOrderBySortOrder(@Param("childId") Long childId);

    @Query("SELECT o FROM GoalOrder o JOIN FETCH o.goal WHERE o.childId = :childId AND o.goal.id IN :goalIds")
    List<GoalOrder> findByChildIdAndGoalIds(@Param("childId") Long childId, @Param("goalIds") List<Long> goalIds);

    @Query("SELECT COALESCE(MAX(o.sortOrder), 0) FROM GoalOrder o WHERE o.childId = :childId")
    Integer findMaxSortOrderByChildId(@Param("childId") Long childId);
}
