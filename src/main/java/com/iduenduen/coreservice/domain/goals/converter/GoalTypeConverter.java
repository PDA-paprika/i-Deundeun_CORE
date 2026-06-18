package com.iduenduen.coreservice.domain.goals.converter;

import java.util.Arrays;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;

import com.iduenduen.coreservice.domain.goals.enums.GoalType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GoalTypeConverter implements AttributeConverter<GoalType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(GoalType goalType) {
        if (goalType == null) return null;
        return goalType.getCode();
    }

    @Override
    public GoalType convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        return Arrays.stream(GoalType.values())
                .filter(t -> t.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));
    }
}
