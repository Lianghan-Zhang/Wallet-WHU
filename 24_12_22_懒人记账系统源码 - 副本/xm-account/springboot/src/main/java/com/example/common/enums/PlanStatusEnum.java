package com.example.common.enums;

public enum PlanStatusEnum {
    NO_READY("未开始"),
    IN_PROGRESS("进行中"),
    END("已结束"),
    DONE("已完成");

    private String value;

    PlanStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
