package com.iduenduen.coreservice.domain.goals.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iduenduen.coreservice.common.base.BaseEntity;
import com.iduenduen.coreservice.domain.goals.enums.GoalStatus;
import com.iduenduen.coreservice.domain.goals.enums.GoalType;

import com.iduenduen.coreservice.domain.goals.converter.GoalTypeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "child_id", nullable = false)
    private Long childId;

    @Convert(converter = GoalTypeConverter.class)
    @Column(name = "goal_type1", nullable = false)
    private GoalType goalType1;

    @Column(name = "goal_type2")
    private Integer goalType2;

    @Column(name = "goal_type3")
    private Integer goalType3;

    @Column(name = "goal_type4")
    private Integer goalType4;

    @Column(name = "level")
    private Integer level;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "target_amount", nullable = false)
    private Long targetAmount;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "achieved_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal achievedPct = BigDecimal.ZERO;

    @Column(name = "recommended_amount")
    private Long recommendedAmount;

    @Column(name = "recommendation_note", length = 1000)
    private String recommendationNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Goal(Long parentId, Long childId, GoalType goalType1, Integer goalType2, Integer goalType3,
                String name, Long targetAmount, LocalDate targetDate, Integer level) {
        this.parentId = parentId;
        this.childId = childId;
        this.goalType1 = goalType1;
        this.goalType2 = goalType2;
        this.goalType3 = goalType3;
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.level = level;
        this.achievedPct = BigDecimal.ZERO;
        this.status = GoalStatus.ACTIVE;
    }

    public void update(String name, Long targetAmount, LocalDate targetDate,
                       GoalType goalType1, Integer goalType2, Integer goalType3) {
        if (name != null) this.name = name;
        if (targetAmount != null) this.targetAmount = targetAmount;
        if (targetDate != null) this.targetDate = targetDate;
        if (goalType1 != null) this.goalType1 = goalType1;
        if (goalType2 != null) this.goalType2 = goalType2;
        if (goalType3 != null) this.goalType3 = goalType3;
    }

    public void updateAchievedPct(BigDecimal achievedPct) {
        this.achievedPct = achievedPct;
    }

    public void applyRecommendation(Long recommendedAmount, String recommendationNote) {
        this.recommendedAmount = recommendedAmount;
        this.recommendationNote = recommendationNote;
    }

    public void updateLevel(int level) {
        this.level = level;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = GoalStatus.CANCELLED;
    }

    public void expire() {
        this.status = GoalStatus.FAILED;
    }
}
