package com.iduenduen.coreservice.common.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Map;

@Component
public class MtsEtfClient {

    private final RestClient restClient = RestClient.create();

    @Value("${mts.api.base-url}")
    private String mtsBaseUrl;

    public String getEtfCodeById(Long etfId) {
        String url = mtsBaseUrl + "/etf/id/" + etfId;
        try {
            Map<?, ?> body = restClient.get().uri(url).retrieve().body(Map.class);
            if (body == null) return null;
            Map<?, ?> data = (Map<?, ?>) body.get("data");
            if (data == null) return null;
            return (String) data.get("etfCode");
        } catch (Exception e) {
            return null;
        }
    }

    public record EtfInfo(String etfCode, String etfName) {}

    public EtfInfo getEtfInfoById(Long etfId) {
        if (etfId == null) return null;
        String url = mtsBaseUrl + "/etf/id/" + etfId;
        try {
            Map<?, ?> body = restClient.get().uri(url).retrieve().body(Map.class);
            if (body == null) return null;
            Map<?, ?> data = (Map<?, ?>) body.get("data");
            if (data == null) return null;
            return new EtfInfo((String) data.get("etfCode"), (String) data.get("etfName"));
        } catch (Exception e) {
            return null;
        }
    }

    public Long getValuationAverage(String etfCode, LocalDate from, LocalDate to) {
        String url = UriComponentsBuilder
                .fromUriString(mtsBaseUrl + "/etf/{etfCode}/valuation-average")
                .queryParam("from", from)
                .queryParam("to", to)
                .buildAndExpand(etfCode)
                .toUriString();

        try {
            Map<?, ?> body = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Map.class);

            if (body == null) return null;
            Map<?, ?> data = (Map<?, ?>) body.get("data");
            if (data == null) return null;
            Number avg = (Number) data.get("averagePrice");
            return avg != null ? avg.longValue() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
