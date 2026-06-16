package com.iduenduen.coreservice.domain.children.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.children.entity.Children;
import com.iduenduen.coreservice.domain.children.enums.CreatedVia;
import com.iduenduen.coreservice.domain.children.enums.Gender;

public record ChildrenDetailResponse(
	@JsonProperty("child_id") Long childId,
	String name,
	@JsonProperty("birth_date") LocalDate birthDate,
	int age,
	Gender gender,
	@JsonProperty("birth_order") int birthOrder,
	@JsonProperty("profile_image_url") String profileImageUrl,
	@JsonProperty("securities_account") String securitiesAccount,
	@JsonProperty("allowance_linked") boolean allowanceLinked,
	@JsonProperty("created_via") CreatedVia createdVia,
	@JsonProperty("created_at") LocalDateTime createdAt
) {
	public static ChildrenDetailResponse from(Children children) {
		return new ChildrenDetailResponse(
			children.getId(),
			children.getName(),
			children.getBirthDate(),
			children.getAge(),
			children.getGender(),
			children.getBirthOrder(),
			children.getProfileImageUrl(),
			children.getSecuritiesAccount(),
			children.isAllowanceLinked(),
			children.getCreatedVia(),
			children.getCreatedAt()
		);
	}
}