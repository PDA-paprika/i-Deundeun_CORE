package com.iduenduen.coreservice.domain.goals.entity;

import java.time.LocalDateTime;

import com.iduenduen.coreservice.common.base.BaseEntity;
import com.iduenduen.coreservice.domain.goals.converter.GoalTypeConverter;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assistant_goal_names")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssistantGoalName extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Convert(converter = GoalTypeConverter.class)
    @Column(name = "goal_type1", nullable = false)
    private GoalType goalType1;

    @Column(name = "goal_type2", nullable = false)
    private Integer goalType2;

    @Column(name = "goal_type3", nullable = false)
    private Integer goalType3;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public AssistantGoalName(Long parentId, GoalType goalType1, Integer goalType2, Integer goalType3, String name) {
        this.parentId = parentId;
        this.goalType1 = goalType1;
        this.goalType2 = goalType2;
        this.goalType3 = goalType3;
        this.name = name;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
