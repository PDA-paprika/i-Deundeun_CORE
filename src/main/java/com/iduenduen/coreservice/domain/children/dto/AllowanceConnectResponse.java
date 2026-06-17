package com.iduenduen.coreservice.domain.children.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllowanceConnectResponse(
	@JsonProperty("linked_account_number") String linkedAccountNumber,
	String status
) {
}