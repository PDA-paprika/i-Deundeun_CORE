package com.iduenduen.coreservice.domain.gift.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.enums.ContractStatus;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;

public record GiftContractListResponse(List<GiftContractItem> contracts) {

    public record GiftContractItem(
            @JsonProperty("contract_id") Long contractId,
            @JsonProperty("gift_type") GiftType giftType,
            ContractStatus status,
            String title,
            @JsonProperty("cash_amount") Long cashAmount,
            Integer qty,
            @JsonProperty("expected_total_amount") Long expectedTotalAmount,
            @JsonProperty("transfer_count") int transferCount,
            @JsonProperty("start_date") LocalDate startDate,
            @JsonProperty("end_date") LocalDate endDate
    ) {
        public static GiftContractItem from(GiftContract contract, int transferCount) {
            long expectedTotal = switch (contract.getGiftType()) {
                case INSTALLMENT -> contract.getCashAmount() * transferCount;
                case ONE_TIME -> contract.getCashAmount();
                case ETF -> 0L;
            };
            return new GiftContractItem(
                    contract.getId(),
                    contract.getGiftType(),
                    contract.getStatus(),
                    contract.getTitle(),
                    contract.getCashAmount(),
                    contract.getQty(),
                    expectedTotal,
                    transferCount,
                    contract.getStartDate(),
                    contract.getEndDate()
            );
        }
    }
}