package com.localapp.mgmt.userprofile.util;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.AttributeConverter;

public class DateStringConverter implements AttributeConverter<LocalDate, String> {
    @Autowired
    KmsUtil kmsUtil;

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return attribute != null ? kmsUtil.kmsEncrypt(attribute.toString()) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return dbData != null ? LocalDate.parse(kmsUtil.kmsDecrypt(dbData)) : null;
    }

}
