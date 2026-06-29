package com.iduenduen.coreservice.domain.account.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AccountEtfHistoriesResponse {
    private int totalCount;
    private List<HistoryDto> histories;

    @Getter
    @Builder
    public static class HistoryDto {
        private Long id;
        private Long etfId;
        private String eventType;
        private String etfName;
        private String logoImg;
        private int qtyDelta;
        private Long price;
        private LocalDateTime occurredAt;
    }
}