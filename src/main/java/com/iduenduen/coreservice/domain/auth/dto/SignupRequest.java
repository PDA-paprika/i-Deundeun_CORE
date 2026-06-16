package com.iduenduen.coreservice.domain.auth.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    private String email;
    private String password;

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
