package com.localapp.mgmt.userprofile.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> listOfValues) {
        return listOfValues != null ? String.join(SPLIT_CHAR, listOfValues) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String values) {
        return values != null ? Arrays.asList(values.split(SPLIT_CHAR)) : Collections.emptyList();
    }
}
