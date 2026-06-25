package com.iduenduen.coreservice.domain.executionGoalLink.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "execution_goal_links")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExecutionGoalLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "child_id")
    private Long childId;

    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "etf_history_id", nullable = false)
    private Long etfHistoryId;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "memo", length = 100)
    private String memo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "linked_at")
    private LocalDateTime linkedAt;

    @Builder
    public ExecutionGoalLink(Long parentId, Long etfHistoryId, int qty) {
        this.parentId = parentId;
        this.etfHistoryId = etfHistoryId;
        this.qty = qty;
    }

    public void link(Long childId, Long goalId, String memo) {
        if (childId == null || goalId == null) return;
        this.childId = childId;
        this.goalId = goalId;
        this.memo = memo;
        this.linkedAt = LocalDateTime.now();
    }

    public void deductQty(int amount) {
        this.qty -= amount;
    }

    public void move(Long childId, Long goalId) {
        this.childId = childId;
        this.goalId = goalId;
        this.linkedAt = LocalDateTime.now();
    }
}
