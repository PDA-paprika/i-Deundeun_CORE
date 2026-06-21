package com.iduenduen.coreservice.domain.children.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.children.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ChildrenCreateRequest(

	@NotBlank
	@Size(max = 50)
	String name,

	@NotNull
	@PastOrPresent(message = "생년월일은 오늘 이전 날짜여야 합니다.")
	@JsonFormat(pattern = "yyyy.MM.dd")
	@JsonProperty("birth_date")
	LocalDate birthDate,

	@NotNull
	@Positive
	@JsonProperty("birth_order")
	Integer birthOrder,

	@NotNull
	Gender gender,

	@NotBlank
	@Size(max = 20)
	@JsonProperty("securities_account")
	String securitiesAccount,

	@Size(max = 500)
	@JsonProperty("profile_image_url")
	String profileImageUrl
) {
}
