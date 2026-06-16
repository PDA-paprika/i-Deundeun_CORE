package com.iduenduen.coreservice.common.status;

import org.springframework.http.HttpStatus;

import com.iduenduen.coreservice.common.base.BaseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    /**
     * Common
     */
    BAD_REQUEST("COMM_400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("COMM_401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN("COMM_403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND("COMM_404", HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("COMM_405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    CONFLICT("COMM_409", HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    INTERNAL_SERVER_ERROR("COMM_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    /**
     * Auth
     */
    INVALID_CREDENTIALS("AUTH_401", HttpStatus.UNAUTHORIZED, "계좌번호 또는 비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL("AUTH_409", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_ACCOUNT_NUMBER("AUTH_409", HttpStatus.CONFLICT, "이미 사용 중인 계좌번호입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
