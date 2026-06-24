package com.iduenduen.coreservice.domain.gift.dto;

import java.time.LocalDate;
import java.time.YearMonth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GiftContractCreateRequest(
	@NotNull
	@JsonProperty("gift_type")
	GiftType giftType,

	String title,

	@JsonProperty("cash_amount")
	Long cashAmount,

	@Min(1)
	@Max(28)  //현재는 1-28일 까지 29-31은 고려 안함
	@JsonProperty("transfer_day")
	Integer transferDay,

	// INSTALLMENT는 서버에서 계산, ONE_TIME·ETF만 프론트에서 전달
	@JsonProperty("start_date")
	LocalDate startDate,

	// INSTALLMENT 전용: 종료 연월 (yyyy-MM)
	@JsonFormat(pattern = "yyyy.MM")
	@JsonProperty("end_month")
	YearMonth endMonth,

	//mts server와 연결 예정
	@JsonProperty("external_etf_id")
	Long externalEtfId,

	@JsonProperty("etf_code")
	String etfCode,

	Integer qty
) {
}
