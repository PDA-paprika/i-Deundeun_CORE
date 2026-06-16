package com.iduenduen.coreservice.domain.children.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import com.iduenduen.coreservice.common.base.BaseEntity;
import com.iduenduen.coreservice.domain.children.enums.CreatedVia;
import com.iduenduen.coreservice.domain.children.enums.Gender;

import jakarta.persistence.Column;
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
@Table(name = "children")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Children extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "parent_id", nullable = false)
	private Long parentId;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = false, length = 10)
	private Gender gender;

	@Column(name = "birth_order", nullable = false)
	private int birthOrder = 1;

	@Column(name = "securities_account", nullable = false, length = 20)
	private String securitiesAccount;

	@Column(name = "profile_image_url", length = 500)
	private String profileImageUrl;

	//추후 enum으로 해야하면 바꿀 예정
	@Column(name = "customer_segment_code", length = 50)
	private String customerSegmentCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "created_via", nullable = false, length = 20)
	private CreatedVia createdVia = CreatedVia.ONBOARDING;

	@Column(name = "allowance_linked", nullable = false)
	private boolean allowanceLinked = false;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public Children(Long parentId, String name, LocalDate birthDate, Gender gender,
		int birthOrder, String securitiesAccount, String profileImageUrl,
		String customerSegmentCode, CreatedVia createdVia) {
		this.parentId = parentId;
		this.name = name;
		this.birthDate = birthDate;
		this.gender = gender;
		this.birthOrder = birthOrder;
		this.securitiesAccount = securitiesAccount;
		this.profileImageUrl = profileImageUrl;
		this.customerSegmentCode = customerSegmentCode;
		this.createdVia = createdVia != null ? createdVia : CreatedVia.ONBOARDING;
	}

	//만나이 계산
	public int getAge() {
		return Period.between(this.birthDate, LocalDate.now()).getYears();
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}

	public void update(String name, LocalDate birthDate, Gender gender, String securitiesAccount, String profileImageUrl) {
		if (name != null) this.name = name;
		if (birthDate != null) this.birthDate = birthDate;
		if (gender != null) this.gender = gender;
		if (securitiesAccount != null) this.securitiesAccount = securitiesAccount;
		if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
	}
}