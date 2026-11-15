package com.campuscare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Audit log entry for tracking full complaint transparency and state transitions.
 */
public class ComplaintHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int historyId;
    private String complaintId;
    private String statusFrom;
    private String statusTo;
    private String remarks;
    private String changedByName;
    private String changedByRole;
    private LocalDateTime timestamp;

    public ComplaintHistory() {
        this.timestamp = LocalDateTime.now();
    }

    public ComplaintHistory(String complaintId, String statusFrom, String statusTo,
                            String remarks, String changedByName, String changedByRole) {
        this.complaintId = complaintId;
        this.statusFrom = statusFrom;
        this.statusTo = statusTo;
        this.remarks = remarks;
        this.changedByName = changedByName;
        this.changedByRole = changedByRole;
        this.timestamp = LocalDateTime.now();
    }

    public ComplaintHistory(int historyId, String complaintId, String statusFrom, String statusTo,
                            String remarks, String changedByName, String changedByRole, LocalDateTime timestamp) {
        this.historyId = historyId;
        this.complaintId = complaintId;
        this.statusFrom = statusFrom;
        this.statusTo = statusTo;
        this.remarks = remarks;
        this.changedByName = changedByName;
        this.changedByRole = changedByRole;
        this.timestamp = timestamp;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getStatusFrom() {
        return statusFrom;
    }

    public void setStatusFrom(String statusFrom) {
        this.statusFrom = statusFrom;
    }

    public String getStatusTo() {
        return statusTo;
    }

    public void setStatusTo(String statusTo) {
        this.statusTo = statusTo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public String getChangedByRole() {
        return changedByRole;
    }

    public void setChangedByRole(String changedByRole) {
        this.changedByRole = changedByRole;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s -> %s] By %s (%s): %s",
                statusFrom != null ? statusFrom : "INITIAL", statusTo, changedByName, changedByRole, remarks);
    }
}
