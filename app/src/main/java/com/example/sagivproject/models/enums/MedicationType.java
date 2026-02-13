package com.example.sagivproject.models.enums;

public enum MedicationType {
    PILL("כדור"),
    HALF_TABLET("חצי כדור"),
    SYRUP("סירופ"),
    CREAM("משחה"),
    INJECTION("זריקה"),
    VITAMIN("ויטמין"),
    DROPSFORSWALLOWIN("טיפות לבליעה"),
    EYEDROPS("טיפות עיניים"),
    EARDROPS("טיפות לאוזניים");

    private final String displayName;

    MedicationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
