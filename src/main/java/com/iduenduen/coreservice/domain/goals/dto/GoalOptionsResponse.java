package com.iduenduen.coreservice.domain.goals.dto;

import java.util.Arrays;
import java.util.List;

import com.iduenduen.coreservice.domain.goals.enums.GoalType;

public record GoalOptionsResponse(List<GoalTypeOption> types) {

    public record GoalTypeOption(int code, String label) {}

    public static GoalOptionsResponse of() {
        List<GoalTypeOption> types = Arrays.stream(GoalType.values())
            .map(t -> new GoalTypeOption(t.getCode(), t.getLabel()))
            .toList();
        return new GoalOptionsResponse(types);
    }
}
