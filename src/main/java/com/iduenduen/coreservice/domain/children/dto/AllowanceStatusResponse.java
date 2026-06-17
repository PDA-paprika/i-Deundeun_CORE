package com.iduenduen.coreservice.domain.children.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllowanceStatusResponse(
	@JsonProperty("allowance_linked")
	boolean allowanceLinked,

	//계좌는 넣는게 맞을지 추후 논의?
	@JsonProperty("linked_account_number")
	String linkedAccountNumber
) {
}