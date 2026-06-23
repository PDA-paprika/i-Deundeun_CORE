package com.iduenduen.coreservice.domain.goals.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
