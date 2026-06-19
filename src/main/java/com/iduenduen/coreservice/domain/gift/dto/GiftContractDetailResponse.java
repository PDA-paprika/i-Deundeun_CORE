package com.iduenduen.coreservice.domain.gift.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.enums.ContractStatus;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;
import com.iduenduen.coreservice.domain.gift.enums.TransferStatus;

public record GiftContractDetailResponse(
        @JsonProperty("contract_id") Long contractId,
        @JsonProperty("gift_type") GiftType giftType,
        ContractStatus status,
        String title,
        @JsonProperty("cash_amount") Long cashAmount,
        @JsonProperty("transfer_day") Integer transferDay,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate,
        @JsonProperty("external_etf_id") Long externalEtfId,
        Integer qty,
        @JsonProperty("transfer_count") int transferCount,
        List<TransferItem> transfers
) {
    public record TransferItem(
            @JsonProperty("transfer_id") Long transferId,
            @JsonProperty("sequence_no") int sequenceNo,
            @JsonProperty("scheduled_date") LocalDate scheduledDate,
            TransferStatus status,
            @JsonProperty("transferred_cash_amt") long transferredCashAmt,
            @JsonProperty("transferred_etf_qty") int transferredEtfQty,
            @JsonProperty("completed_at") LocalDateTime completedAt
    ) {
        public static TransferItem from(GiftTransfer transfer) {
            return new TransferItem(
                    transfer.getId(),
                    transfer.getSequenceNo(),
                    transfer.getScheduledDate(),
                    transfer.getStatus(),
                    transfer.getTransferredCashAmt(),
                    transfer.getTransferredEtfQty(),
                    transfer.getCompletedAt()
            );
        }
    }

    public static GiftContractDetailResponse of(GiftContract contract, List<GiftTransfer> transfers) {
        return new GiftContractDetailResponse(
                contract.getId(),
                contract.getGiftType(),
                contract.getStatus(),
                contract.getTitle(),
                contract.getCashAmount(),
                contract.getTransferDay(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getExternalEtfId(),
                contract.getQty(),
                transfers.size(),
                transfers.stream().map(TransferItem::from).toList()
        );
    }
}