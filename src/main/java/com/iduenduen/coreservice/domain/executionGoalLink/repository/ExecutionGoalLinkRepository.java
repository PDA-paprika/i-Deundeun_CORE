package com.iduenduen.coreservice.domain.executionGoalLink.repository;

import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecutionGoalLinkRepository extends JpaRepository<ExecutionGoalLink, Long> {
    List<ExecutionGoalLink> findByParentIdAndLinkedAtIsNullOrderByCreatedAtDesc(Long parentId);

    List<ExecutionGoalLink> findByParentIdAndLinkedAtIsNullOrderByCreatedAtAsc(Long parentId);

    List<ExecutionGoalLink> findAllByParentId(Long parentId);

    List<ExecutionGoalLink> findByGoalIdAndChildIdOrderByCreatedAtDesc(Long goalId, Long childId);

    @Query("SELECT h.etfId, SUM(l.qty) FROM ExecutionGoalLink l JOIN AccountEtfHistory h ON l.etfHistoryId = h.id WHERE l.parentId = :parentId GROUP BY h.etfId")
    List<Object[]> sumTaggedQtyByEtfIdForParent(@Param("parentId") Long parentId);

    @Query("SELECT e FROM ExecutionGoalLink e WHERE (e.childId = :childId OR (:childId IS NULL AND e.childId IS NULL)) AND (e.goalId = :goalId OR (:goalId IS NULL AND e.goalId IS NULL)) AND e.qty > 0 ORDER BY e.createdAt ASC")
    List<ExecutionGoalLink> findForFifoDeduction(@Param("childId") Long childId, @Param("goalId") Long goalId);
}
