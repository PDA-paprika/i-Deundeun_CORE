package com.iduenduen.coreservice.domain.account.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AccountCashHistoriesResponse {
    private int totalCount;
    private List<HistoryDto> histories;

    @Getter
    @Builder
    public static class HistoryDto {
        private Long id;
        private String eventType;
        private Long amountDelta;
        private Long balanceAfter;
        private String memo;
        private LocalDateTime occurredAt;
    }
}