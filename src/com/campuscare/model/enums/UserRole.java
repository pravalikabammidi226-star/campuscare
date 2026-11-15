package com.campuscare.model.enums;

/**
 * Represents the distinct system roles for role-based authorization.
 */
public enum UserRole {
    STUDENT("Student"),
    ADMIN("Administrator"),
    STAFF("Department Staff");

    private final String displayName;

    UserRole(String displayName) {
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
