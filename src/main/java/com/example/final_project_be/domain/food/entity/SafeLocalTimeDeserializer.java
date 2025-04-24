package com.example.final_project_be.domain.food.entity;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.jsonwebtoken.io.IOException;

public class SafeLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, java.io.IOException {
        String time = p.getText().split("\\.")[0]; // 나노초 제거
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
