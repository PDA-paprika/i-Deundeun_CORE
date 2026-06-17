package com.iduenduen.coreservice.domain.children.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllowanceStatusResponse(
	@JsonProperty("allowance_linked")
	boolean allowanceLinked
) {
}