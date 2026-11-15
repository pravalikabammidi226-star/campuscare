package com.campuscare.model.enums;

/**
 * Standard categories of campus complaints with associated SLA thresholds and sensitivity flags.
 */
public enum ComplaintCategory {
    CLASSROOM_LAB("Classroom & Laboratory Maintenance", 72, false, 2),
    HOSTEL("Hostel & Residential Facilities", 48, false, 1),
    LIBRARY("Library Facilities & Resources", 72, false, 3),
    CANTEEN_HYGIENE("Canteen Hygiene & Food Quality", 24, false, 5),
    TRANSPORTATION("Campus Transportation & Buses", 48, false, 4),
    WIFI_TECHNICAL("Wi-Fi & IT Technical Support", 48, false, 3),
    WATER_ELECTRICITY("Water & Electricity Services", 24, false, 2),
    RAGGING_HARASSMENT("Ragging, Bullying or Harassment (Confidential)", 12, true, 6),
    OTHER("Other Campus Grievance", 96, false, 0);

    private final String displayName;
    private final int defaultSlaHours;
    private final boolean sensitive;
    private final int suggestedDepartmentId;

    ComplaintCategory(String displayName, int defaultSlaHours, boolean sensitive, int suggestedDepartmentId) {
        this.displayName = displayName;
        this.defaultSlaHours = defaultSlaHours;
        this.sensitive = sensitive;
        this.suggestedDepartmentId = suggestedDepartmentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultSlaHours() {
        return defaultSlaHours;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public int getSuggestedDepartmentId() {
        return suggestedDepartmentId;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
