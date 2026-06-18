package com.iduenduen.coreservice.common.status;

import org.springframework.http.HttpStatus;

import com.iduenduen.coreservice.common.base.BaseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessStatus implements BaseStatus {

    /**
     * Common
     */
    SUCCESS_200("IDEUNDEUN_200", HttpStatus.OK, "성공입니다."),
    SUCCESS_201("IDEUNDEUN_201", HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204("IDEUNDEUN_204", HttpStatus.NO_CONTENT, "성공입니다."),

    /**
     * Children
     */
    CHILDREN_REGISTER_SUCCESS("CHILDREN_201", HttpStatus.CREATED, "자녀 등록이 완료되었습니다."),
    CHILDREN_DELETE_SUCCESS("CHILDREN_200", HttpStatus.OK, "자녀가 삭제되었습니다."),
    CHILDREN_ALLOWANCE_CONNECT_SUCCESS("CHILDREN_ALLOWANCE_200", HttpStatus.OK, "아동수당 연결이 완료되었습니다."),
    /**
     * Gift
     */
    GIFT_CONTRACT_CREATE_SUCCESS("GIFT_201", HttpStatus.CREATED, "증여 계약이 등록되었습니다."),


    /**
     * Email
     */
    EMAIL_SEND_SUCCESS("EMAIL_200", HttpStatus.OK, "인증 코드가 발송되었습니다."),
    EMAIL_VERIFY_SUCCESS("EMAIL_200_01", HttpStatus.OK, "이메일 인증이 완료되었습니다."),

    /**
     * Goals
     */
    GOAL_CREATE_SUCCESS("GOAL_201", HttpStatus.CREATED, "목표가 생성되었습니다."),
    GOAL_DELETE_SUCCESS("GOAL_200", HttpStatus.OK, "목표가 삭제되었습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}