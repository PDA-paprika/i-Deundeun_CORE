package com.iduenduen.coreservice.domain.goals.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iduenduen.coreservice.domain.goals.entity.Goal;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // 자녀 목표 목록
    List<Goal> findAllByChildIdAndParentIdAndStatusAndDeletedAtIsNull(
        Long childId, Long parentId, GoalStatus status);

    // 단건 조회
    Optional<Goal> findByIdAndChildIdAndParentIdAndDeletedAtIsNull(
        Long id, Long childId, Long parentId);

    // FAILED 탐색
    List<Goal> findAllByStatusAndTargetDateBefore(GoalStatus status, LocalDate date);

    List<Goal> findAllByParentId(Long parentId);

    @Query(value =
        "SELECT g.goal_type1 AS goalType1, g.goal_type2 AS goalType2, g.goal_type3 AS goalType3, COUNT(*) AS count " +
        "FROM goals g " +
        "JOIN parents p ON g.parent_id = p.id " +
        "WHERE p.cluster_value = :clusterValue " +
        "  AND p.deleted_at IS NULL " +
        "  AND g.deleted_at IS NULL " +
        "GROUP BY g.goal_type1, g.goal_type2, g.goal_type3 " +
        "ORDER BY count DESC",
        nativeQuery = true)
    List<GoalFrequencyProjection> countByClusterValue(@Param("clusterValue") Integer clusterValue);
}
