package com.iduenduen.coreservice.domain.goals.entity;

import com.iduenduen.coreservice.common.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goal_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_id", nullable = false)
    private Long childId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder
    public GoalOrder(Long childId, Goal goal, Integer sortOrder) {
        this.childId = childId;
        this.goal = goal;
        this.sortOrder = sortOrder;
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
