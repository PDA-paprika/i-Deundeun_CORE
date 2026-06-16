package com.iduenduen.coreservice.domain.parent.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iduenduen.coreservice.common.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parents", uniqueConstraints = {
        @UniqueConstraint(name = "uk_parents_email", columnNames = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Parent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(name = "account_number", length = 20, nullable = false)
    private String accountNumber;

    @Column(name = "selected_child_id", length = 36)
    private String selectedChildId;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(length = 10, nullable = false)
    private String relation;

    @Column(length = 30, nullable = false)
    private String region;

    @Column(name = "child_count", nullable = false)
    private Integer childCount;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "income_level", length = 20)
    private String incomeLevel;

    @Column(name = "asset_range", length = 30)
    private String assetRange;

    @Column(name = "education_heat")
    private Integer educationHeat;

    @Column(name = "dual_income")
    private Boolean dualIncome;

    @Column(name = "cluster_value")
    private Integer clusterValue;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "cert_file_url", length = 500, nullable = false)
    private String certFileUrl;

    @Builder
    public Parent(String email, String accountNumber, String selectedChildId, String passwordHash,
                  String name, LocalDate birthDate, String relation, String region, Integer childCount,
                  String profileImageUrl, String incomeLevel, String assetRange, Integer educationHeat,
                  Boolean dualIncome, Integer clusterValue, String certFileUrl) {
        this.email = email;
        this.accountNumber = accountNumber;
        this.selectedChildId = selectedChildId;
        this.passwordHash = passwordHash;
        this.name = name;
        this.birthDate = birthDate;
        this.relation = relation;
        this.region = region;
        this.childCount = childCount;
        this.profileImageUrl = profileImageUrl;
        this.incomeLevel = incomeLevel;
        this.assetRange = assetRange;
        this.educationHeat = educationHeat;
        this.dualIncome = dualIncome;
        this.clusterValue = clusterValue;
        this.certFileUrl = certFileUrl;
    }

    public void updateProfile(String name, LocalDate birthDate, String relation, Integer childCount,
                               String region, String profileImageUrl) {
        this.name = name;
        this.birthDate = birthDate;
        this.relation = relation;
        this.childCount = childCount;
        this.region = region;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateSelectedChild(String selectedChildId) {
        this.selectedChildId = selectedChildId;
    }

    public void updateWizardProfile(String incomeLevel, String assetRange, Integer educationHeat,
                                     Boolean dualIncome) {
        this.incomeLevel = incomeLevel;
        this.assetRange = assetRange;
        this.educationHeat = educationHeat;
        this.dualIncome = dualIncome;
    }

    public void withdraw() {
        this.deletedAt = LocalDateTime.now();
    }
}
