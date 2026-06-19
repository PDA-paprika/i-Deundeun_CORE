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

	String message,

	@JsonProperty("expected_total_amount")
	Long expectedTotalAmount,

	@JsonProperty("transfer_count")
	int transferCount
) {
	public static String messageFrom(ContractStatus status) {
		return switch (status) {
			case DRAFT -> "증여 계약이 임시 저장되었습니다.";
			case ACTIVE -> "증여 계약이 등록되었습니다.";
			case COMPLETED -> "증여가 완료되었습니다.";
			case CANCELLED -> "증여 계약이 취소되었습니다.";
			case FAILED -> "증여 처리에 실패하였습니다.";
		};
	}
}
