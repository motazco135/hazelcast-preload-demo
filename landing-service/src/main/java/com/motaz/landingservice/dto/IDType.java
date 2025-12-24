package com.motaz.landingservice.dto;

public enum IDType {
    IQAMA(2),
    NIN(1);

    private final int value;

    IDType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
