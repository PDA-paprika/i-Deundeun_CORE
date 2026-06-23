package com.iduenduen.coreservice.domain.children.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ChildHoldingsResponse {

    private List<HoldingDto> holdings;

    @Getter
    @Builder
    public static class HoldingDto {

        @JsonProperty("etf_id")
        private Long etfId;

        private Integer qty;

        @JsonProperty("avg_buy_price")
        private Long avgBuyPrice;
    }
}