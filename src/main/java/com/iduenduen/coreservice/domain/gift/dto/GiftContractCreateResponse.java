package com.iduenduen.coreservice.domain.gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.gift.enums.ContractStatus;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;

public record GiftContractCreateResponse(

	@JsonProperty("contract_id")
	Long contractId,

	@JsonProperty("gift_type")
	GiftType giftType,

	ContractStatus status,

	@JsonProperty("expected_total_amount")
	Long expectedTotalAmount,

	@JsonProperty("transfer_count")
	int transferCount
) {
}
