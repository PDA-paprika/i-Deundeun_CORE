package com.iduenduen.coreservice.domain.goals.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalType {
    EDUCATION(1, "교육"),
    LIFE(2, "생활"),
    MARRIAGE(3, "결혼"),
    GIFT(4, "증여"),
    CUSTOM(0, "직접선택");

    private final int code;
    private final String label;
}
