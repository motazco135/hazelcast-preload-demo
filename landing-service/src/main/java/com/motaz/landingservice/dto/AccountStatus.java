package com.motaz.landingservice.dto;

public enum AccountStatus {
    ACTIVE(1),
    DORMANT(2),
    CLOSED(3);

    private final int value;

    AccountStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
