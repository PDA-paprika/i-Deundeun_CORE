package com.iduenduen.coreservice.domain.executionGoalLink.repository;

import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecutionGoalLinkRepository extends JpaRepository<ExecutionGoalLink, Long> {
    List<ExecutionGoalLink> findByParentIdAndChildIdIsNullAndGoalIdIsNullOrderByCreatedAtDesc(Long parentId);

    List<ExecutionGoalLink> findAllByParentId(Long parentId);

    List<ExecutionGoalLink> findByGoalIdAndChildIdOrderByCreatedAtDesc(Long goalId, Long childId);

    @Query(value = "SELECT h.etf_id, SUM(l.qty) FROM execution_goal_links l JOIN account_etf_histories h ON l.etf_history_id = h.id WHERE l.parent_id = :parentId AND l.child_id IS NOT NULL AND l.goal_id IS NOT NULL GROUP BY h.etf_id", nativeQuery = true)
    List<Object[]> sumTaggedQtyByEtfIdForParent(@Param("parentId") Long parentId);

    @Query(value = "SELECT h.etf_id, h.etf_name_snapshot, SUM(l.qty) FROM execution_goal_links l JOIN account_etf_histories h ON l.etf_history_id = h.id WHERE l.goal_id = :goalId AND l.child_id = :childId GROUP BY h.etf_id, h.etf_name_snapshot", nativeQuery = true)
    List<Object[]> sumQtyByGoalAndChild(@Param("goalId") Long goalId, @Param("childId") Long childId);

    @Query(value = "SELECT l.goal_id, SUM(l.qty * h.price) FROM execution_goal_links l JOIN account_etf_histories h ON l.etf_history_id = h.id WHERE l.goal_id IN :goalIds GROUP BY l.goal_id", nativeQuery = true)
    List<Object[]> sumInvestedAmtByGoalIds(@Param("goalIds") List<Long> goalIds);

    @Query("SELECT e FROM ExecutionGoalLink e WHERE (e.childId = :childId OR (:childId IS NULL AND e.childId IS NULL)) AND (e.goalId = :goalId OR (:goalId IS NULL AND e.goalId IS NULL)) AND e.qty > 0 ORDER BY e.createdAt ASC")
    List<ExecutionGoalLink> findForFifoDeduction(@Param("childId") Long childId, @Param("goalId") Long goalId);

    @Modifying
    @Query("UPDATE ExecutionGoalLink e SET e.childId = NULL, e.goalId = NULL, e.linkedAt = CURRENT_TIMESTAMP WHERE e.goalId = :goalId AND e.qty > 0")
    void unlinkByGoalId(@Param("goalId") Long goalId);
}
