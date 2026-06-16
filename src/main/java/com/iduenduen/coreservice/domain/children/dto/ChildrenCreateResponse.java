package com.iduenduen.coreservice.domain.children.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.children.entity.Children;
import com.iduenduen.coreservice.domain.children.enums.CreatedVia;
import com.iduenduen.coreservice.domain.children.enums.Gender;

public record ChildrenCreateResponse(

	Long id,

	@JsonProperty("parent_id")
	Long parentId,

	String name,

	@JsonProperty("birth_date")
	LocalDate birthDate,

	@JsonProperty("birth_order")
	int birthOrder,

	Gender gender,

	@JsonProperty("securities_account")
	String securitiesAccount,

	@JsonProperty("profile_image_url")
	String profileImageUrl,

	@JsonProperty("customer_segment_code")
	String customerSegmentCode,

	@JsonProperty("created_via")
	CreatedVia createdVia
) {
	public static ChildrenCreateResponse from(Children children) {
		return new ChildrenCreateResponse(
			children.getId(),
			children.getParentId(),
			children.getName(),
			children.getBirthDate(),
			children.getBirthOrder(),
			children.getGender(),
			children.getSecuritiesAccount(),
			children.getProfileImageUrl(),
			children.getCustomerSegmentCode(),
			children.getCreatedVia()
		);
	}
}