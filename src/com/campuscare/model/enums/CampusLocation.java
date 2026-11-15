package com.campuscare.model.enums;

/**
 * Standard designated campus zones and locations for complaints and heatmap analytics.
 */
public enum CampusLocation {
    ACADEMIC_BLOCK_A("Academic Block A (CSE & IT)"),
    ACADEMIC_BLOCK_B("Academic Block B (ECE & EEE)"),
    MECHANICAL_WORKSHOP("Mechanical & Civil Workshop"),
    CENTRAL_LIBRARY("Central Library & Digital Resource Center"),
    HOSTEL_BLOCK_A("Hostel Block A (Boys Senior)"),
    HOSTEL_BLOCK_B("Hostel Block B (Boys Junior)"),
    HOSTEL_BLOCK_C("Hostel Block C (Girls)"),
    STUDENT_CANTEEN("Central Student Cafeteria & Food Court"),
    SPORTS_COMPLEX("Indoor Sports Complex & Gymnasium"),
    AUDITORIUM("Main University Auditorium"),
    ADMINISTRATIVE_BLOCK("Administrative Block & Dean Office"),
    NORTH_GATE("North Gate Parking & Bus Bay"),
    SOUTH_GATE("South Gate Entrance & Transit Point");

    private final String displayName;

    CampusLocation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
