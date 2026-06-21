package com.iduenduen.coreservice.domain.gift.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GiftTitleUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String title;
}