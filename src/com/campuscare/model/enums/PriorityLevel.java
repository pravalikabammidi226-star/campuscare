package com.campuscare.model.enums;

/**
 * Priority levels indicating complaint urgency and resolution deadlines.
 */
public enum PriorityLevel {
    NORMAL("Normal", 168),     // 7 days SLA
    HIGH("High", 72),          // 3 days SLA
    URGENT("Urgent", 24);      // 24 hours SLA

    private final String displayName;
    private final int slaHours;

    PriorityLevel(String displayName, int slaHours) {
        this.displayName = displayName;
        this.slaHours = slaHours;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSlaHours() {
        return slaHours;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
