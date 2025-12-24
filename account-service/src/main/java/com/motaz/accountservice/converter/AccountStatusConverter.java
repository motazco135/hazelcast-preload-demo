package com.motaz.accountservice.converter;

import com.motaz.accountservice.dto.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccountStatus accountStatus) {
        if (accountStatus == null) {
            return null;
        }
        return accountStatus.getValue();
    }
    @Override
    public AccountStatus convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }

        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.getValue() == value) {
                return accountStatus;
            }
        }
        throw new IllegalArgumentException("Unknown ID Type value: " + value);
    }
}
