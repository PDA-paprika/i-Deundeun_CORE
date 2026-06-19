package com.iduenduen.coreservice.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
        @NotBlank
		String email,

        @NotBlank
		@JsonProperty("new_password")
		String newPassword
) {}
