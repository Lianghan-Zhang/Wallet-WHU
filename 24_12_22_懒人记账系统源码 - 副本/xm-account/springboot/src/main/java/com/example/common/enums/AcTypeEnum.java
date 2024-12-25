package com.example.common.enums;

public enum AcTypeEnum {
    PAY("支出"),
    IN_COME("收入");

    private String value;

    public String getValue() {
        return value;
    }

    AcTypeEnum(String value) {
        this.value = value;
    }
}
