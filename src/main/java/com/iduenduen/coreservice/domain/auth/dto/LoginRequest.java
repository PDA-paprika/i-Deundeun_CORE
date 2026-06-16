package com.iduenduen.coreservice.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @JsonProperty("account_number")
    private String accountNumber;

    private String password;
}
