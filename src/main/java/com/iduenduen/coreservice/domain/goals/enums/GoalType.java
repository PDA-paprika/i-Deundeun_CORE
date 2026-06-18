package com.iduenduen.coreservice.domain.goals.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalType {
    LIFE(1, "생활"),
    EDUCATION(2, "교육"),
    GIFT(3, "증여"),
    MARRIAGE(4, "결혼");

    private final int code;
    private final String label;
}
