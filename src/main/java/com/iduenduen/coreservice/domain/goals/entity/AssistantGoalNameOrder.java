package com.iduenduen.coreservice.domain.goals.entity;

import com.iduenduen.coreservice.common.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assistant_goal_name_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssistantGoalNameOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false, foreignKey = @ForeignKey(name = "FK8xj3x3x7wph4y2xat90bsojmx", foreignKeyDefinition = "FOREIGN KEY (goal_id) REFERENCES assistant_goal_names(id) ON DELETE CASCADE"))
    private AssistantGoalName goal;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder
    public AssistantGoalNameOrder(Long parentId, AssistantGoalName goal, Integer sortOrder) {
        this.parentId = parentId;
        this.goal = goal;
        this.sortOrder = sortOrder;
    }

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
