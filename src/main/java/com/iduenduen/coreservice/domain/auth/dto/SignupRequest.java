package com.iduenduen.coreservice.domain.auth.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.onboarding.enums.AgreementType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    private String email;
    private String password;

    private List<AgreementItem> agreements;

    @Getter
    @NoArgsConstructor
    public static class AgreementItem {
        private AgreementType type;
        private Boolean agreed;
    }

    @JsonProperty("account_number")
    private String accountNumber;

    private String name;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String relation;
    private String region;

    @JsonProperty("child_count")
    private Integer childCount;

    @JsonProperty("cert_file_url")
    private String certFileUrl;
}
