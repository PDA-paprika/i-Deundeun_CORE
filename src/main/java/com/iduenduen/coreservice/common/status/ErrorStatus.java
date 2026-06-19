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
    INTERNAL_SERVER_ERROR("COMM_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    CONFLICT("COMM_409", HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

    /**
     * Account
     */
    ACCOUNT_NOT_FOUND("ACC_404", HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다."),
    ACCOUNT_ACCESS_DENIED("ACC_403", HttpStatus.FORBIDDEN, "해당 계좌에 대한 접근 권한이 없습니다."),

    /**
     * Parent
     */
    PARENT_NOT_FOUND("PARENT_404", HttpStatus.NOT_FOUND, "존재하지 않는 부모입니다."),

    /**
     * Children
     */
    CHILDREN_NOT_FOUND("CHILDREN_404", HttpStatus.NOT_FOUND, "존재하지 않는 자녀입니다."),
    CHILDREN_DUPLICATE_NAME("CHILDREN_409", HttpStatus.CONFLICT, "이미 등록된 자녀 이름입니다."),
    CHILDREN_ALLOWANCE_ALREADY_LINKED("CHILDREN_409_01", HttpStatus.CONFLICT, "이미 아동수당이 연결되어 있습니다."),

    /**
     * Gift
     */
    GIFT_CONTRACT_INVALID_FIELDS("GIFT_400", HttpStatus.BAD_REQUEST, "증여 유형에 맞는 필드를 입력해주세요."),
    GIFT_CONTRACT_ONLY_TODAY("GIFT_400_03", HttpStatus.BAD_REQUEST, "일시금 및 ETF 증여는 당일만 가능합니다."),
    INSUFFICIENT_BALANCE("GIFT_400_01", HttpStatus.BAD_REQUEST, "계좌 잔액이 부족합니다."),
    INSUFFICIENT_ETF_QTY("GIFT_400_02", HttpStatus.BAD_REQUEST, "보유 ETF 수량이 부족합니다."),


    /**
     * Auth
     */
    INVALID_CREDENTIALS("AUTH_401", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN("AUTH_401_02", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    DUPLICATE_EMAIL("AUTH_409_01", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_ACCOUNT_NUMBER("AUTH_409_02", HttpStatus.CONFLICT, "이미 사용 중인 계좌번호입니다."),

    /**
     * Onboarding
     */
    REQUIRED_AGREEMENTS_NOT_AGREED("ONBOARD_400", HttpStatus.BAD_REQUEST, "필수 약관에 동의해야 합니다."),

    /**
     * Goals
     */
    GOAL_NOT_FOUND("GOAL_404", HttpStatus.NOT_FOUND, "존재하지 않는 목표입니다."),

    /**
     * Email
     */
    EMAIL_SEND_FAILED("EMAIL_500", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    EMAIL_NOT_VERIFIED("EMAIL_401", HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다."),
    EMAIL_VERIFICATION_NOT_FOUND("EMAIL_404", HttpStatus.NOT_FOUND, "이메일 인증 정보를 찾을 수 없습니다."),
    VERIFICATION_CODE_EXPIRED("EMAIL_410", HttpStatus.GONE, "인증 코드가 만료되었습니다."),
    INVALID_VERIFICATION_CODE("EMAIL_400", HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다."),

    /**
     * Upload
     */
    INVALID_FILE_TYPE("UPLOAD_400", HttpStatus.BAD_REQUEST, "허용되지 않는 파일 형식입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
