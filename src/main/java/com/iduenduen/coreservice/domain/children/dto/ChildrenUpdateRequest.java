package com.iduenduen.coreservice.domain.children.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.children.enums.Gender;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record ChildrenUpdateRequest(
	@Size(max = 50)
	String name,

	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty("birth_date")
	@PastOrPresent(message = "생년월일은 오늘 이전 날짜여야 합니다.")
	LocalDate birthDate,

	Gender gender,

	@JsonProperty("securities_account")
	@Size(max = 20)
	String securitiesAccount,

	@JsonProperty("profile_image_url")
	@Size(max = 500)
	String profileImageUrl
) {
}
