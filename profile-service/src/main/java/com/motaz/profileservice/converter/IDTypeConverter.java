package com.motaz.profileservice.converter;

import com.motaz.profileservice.dto.IDType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IDTypeConverter implements AttributeConverter<IDType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(IDType idType) {
        if (idType == null) {
            return null;
        }
        return idType.getValue();
    }

    @Override
    public IDType convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        
        for (IDType idType : IDType.values()) {
            if (idType.getValue() == value) {
                return idType;
            }
        }
        
        throw new IllegalArgumentException("Unknown ID Type value: " + value);
    }
}
