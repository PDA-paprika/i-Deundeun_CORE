package com.iduenduen.coreservice.domain.goals.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    /** JSON 직렬화 시 항상 정수 code 로 내보낸다 (프론트/통계/DB 와 동일 기준). */
    @JsonValue
    public int toJson() {
        return code;
    }

    /** code(정수 또는 숫자 문자열) 또는 enum 이름 어느 쪽으로 들어와도 매핑한다. */
    @JsonCreator
    public static GoalType from(Object value) {
        if (value == null) {
            return null;
        }
        String raw = value.toString().trim();
        if (raw.isEmpty()) {
            return null;
        }
        try {
            int code = Integer.parseInt(raw);
            return Arrays.stream(values())
                .filter(t -> t.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown GoalType code: " + code));
        } catch (NumberFormatException notNumeric) {
            return GoalType.valueOf(raw.toUpperCase());
        }
    }
}
