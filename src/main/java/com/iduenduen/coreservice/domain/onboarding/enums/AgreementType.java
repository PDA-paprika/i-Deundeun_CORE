package com.iduenduen.coreservice.domain.onboarding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgreementType {

    TERMS("terms001", "이용약관", true),
    PRIVACY("terms002", "개인정보처리방침", true),
    MARKETING("terms003", "마케팅 수신 동의", false);

    private final String code;
    private final String title;
    private final boolean required;
}
