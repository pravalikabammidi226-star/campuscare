package com.campuscare.model.enums;

/**
 * Types of credit transactions awarded to students in the CampusCare Credit Wallet.
 */
public enum TransactionType {
    COMPLAINT_SUBMISSION("Valid Complaint Submission", 5),
    SUPPLEMENTARY_INFO("Supplementary Evidence/Info", 3),
    RESOLUTION_FEEDBACK("Resolution Feedback Given", 2),
    VERIFIED_SAFETY_BONUS("Verified Safety/Maintenance Issue", 5),
    VERIFICATION_HELP("Resolved Issue Verification", 3),
    MANUAL_ADJUSTMENT("Administrator Adjustment", 0);

    private final String description;
    private final int defaultPoints;

    TransactionType(String description, int defaultPoints) {
        this.description = description;
        this.defaultPoints = defaultPoints;
    }

    public String getDescription() {
        return description;
    }

    public int getDefaultPoints() {
        return defaultPoints;
    }

    @Override
    public String toString() {
        return description + " (+" + defaultPoints + " pts)";
    }
}
