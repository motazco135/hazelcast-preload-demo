package com.motaz.accountservice.dto;

public enum AccountType {
    CURRENT(1),
    SAVINGS(2);
    private final int value;

    AccountType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
