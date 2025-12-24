package com.motaz.accountservice.converter;

import com.motaz.accountservice.dto.AccountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<AccountType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return accountType.getValue();
    }
    @Override
    public AccountType convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }

        for (AccountType accountType : AccountType.values()) {
            if (accountType.getValue() == value) {
                return accountType;
            }
        }
        throw new IllegalArgumentException("Unknown ID Type value: " + value);
    }
}
