package com.iduenduen.coreservice.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * yyyy.MM 또는 yyyy.MM.dd 형식을 모두 LocalDate로 파싱.
 * yyyy.MM 입력 시 해당 월의 1일로 처리.
 */
public class FlexibleLocalDateDeserializer extends StdDeserializer<LocalDate> {

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM");
    private static final DateTimeFormatter FULL_DATE_FORMAT  = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public FlexibleLocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getText().trim();
        if (value.length() == 7) {
            return LocalDate.parse(value, YEAR_MONTH_FORMAT).withDayOfMonth(1);
        }
        return LocalDate.parse(value, FULL_DATE_FORMAT);
    }
}
