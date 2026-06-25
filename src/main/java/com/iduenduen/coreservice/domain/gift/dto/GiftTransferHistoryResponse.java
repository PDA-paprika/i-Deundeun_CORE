package com.iduenduen.coreservice.domain.gift.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;
import com.iduenduen.coreservice.domain.gift.enums.TransferStatus;

public record GiftTransferHistoryResponse(List<TransferHistoryItem> transfers) {

    public record TransferHistoryItem(
            @JsonProperty("transfer_id") Long transferId,
            @JsonProperty("contract_id") Long contractId,
            @JsonProperty("gift_type") GiftType giftType,
            String title,
            @JsonProperty("sequence_no") int sequenceNo,
            @JsonProperty("scheduled_date") LocalDate scheduledDate,
            TransferStatus status,
            @JsonProperty("transferred_cash_amt") long transferredCashAmt,
            @JsonProperty("transferred_etf_qty") int transferredEtfQty,
            @JsonProperty("estimated_gift_amount") Long estimatedGiftAmount,
            @JsonProperty("final_gift_amount") Long finalGiftAmount,
            @JsonProperty("completed_at") LocalDateTime completedAt
    ) {
        public static TransferHistoryItem of(GiftTransfer transfer, GiftContract contract) {
            boolean isEtf = contract.getGiftType() == GiftType.ETF;
            return new TransferHistoryItem(
                    transfer.getId(),
                    contract.getId(),
                    contract.getGiftType(),
                    contract.getTitle(),
                    transfer.getSequenceNo(),
                    transfer.getScheduledDate(),
                    transfer.getStatus(),
                    transfer.getTransferredCashAmt(),
                    transfer.getTransferredEtfQty(),
                    isEtf ? contract.getEstimatedGiftAmount() : null,
                    isEtf ? contract.getFinalGiftAmount() : null,
                    transfer.getCompletedAt()
            );
        }
    }
}