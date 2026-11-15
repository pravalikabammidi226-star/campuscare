package com.campuscare.model.enums;

/**
 * Lifecycle states of a complaint.
 */
public enum ComplaintStatus {
    SUBMITTED("Submitted", false),
    UNDER_REVIEW("Under Review", false),
    ASSIGNED("Assigned", false),
    IN_PROGRESS("In Progress", false),
    RESOLVED("Resolved", true),
    REJECTED("Rejected", true),
    ESCALATED("Escalated", false),
    CLOSED("Closed", true);

    private final String displayName;
    private final boolean terminal;

    ComplaintStatus(String displayName, boolean terminal) {
        this.displayName = displayName;
        this.terminal = terminal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
