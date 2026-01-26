package com.example.sagivproject.models.enums;

public enum MedicationType {
    PILL("כדור"),
    SYRUP("סירופ"),
    CREAM("משחה"),
    INJECTION("זריקה");

    private final String displayName;

    MedicationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}