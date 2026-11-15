package com.campuscare.model;

import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.ComplaintStatus;
import com.campuscare.model.enums.PriorityLevel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Core Complaint entity representing an issue submitted by a student.
 * Supports anonymous reporting, smart priority calculation, deadline tracking, and lifecycle status.
 */
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private String complaintId;
    private String ticketNumber;
    private String studentId;
    private String studentName;
    private String studentRoll;
    private ComplaintCategory category;
    private String title;
    private String description;
    private String location;
    private PriorityLevel priority;
    private ComplaintStatus status;
    private boolean anonymous;
    private Integer assignedDepartmentId;
    private String assignedDepartmentName;
    private String assignedStaffId;
    private String assignedStaffName;
    private LocalDateTime createdAt;
    private LocalDateTime deadlineAt;
    private LocalDateTime resolvedAt;
    private String rejectionReason;

    public Complaint() {
        this.createdAt = LocalDateTime.now();
        this.status = ComplaintStatus.SUBMITTED;
        this.priority = PriorityLevel.NORMAL;
    }

    // Overloaded Constructor for new complaint submission
    public Complaint(String complaintId, String ticketNumber, String studentId, ComplaintCategory category,
                     String title, String description, String location, PriorityLevel priority,
                     boolean anonymous, LocalDateTime deadlineAt) {
        this.complaintId = complaintId;
        this.ticketNumber = ticketNumber;
        this.studentId = studentId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.location = location;
        this.priority = priority != null ? priority : PriorityLevel.NORMAL;
        this.status = ComplaintStatus.SUBMITTED;
        this.anonymous = anonymous;
        this.createdAt = LocalDateTime.now();
        this.deadlineAt = deadlineAt;
    }

    // Overloaded Constructor with all fields (from Database)
    public Complaint(String complaintId, String ticketNumber, String studentId, String studentName,
                     String studentRoll, ComplaintCategory category, String title, String description,
                     String location, PriorityLevel priority, ComplaintStatus status, boolean anonymous,
                     Integer assignedDepartmentId, String assignedDepartmentName, String assignedStaffId,
                     String assignedStaffName, LocalDateTime createdAt, LocalDateTime deadlineAt,
                     LocalDateTime resolvedAt, String rejectionReason) {
        this.complaintId = complaintId;
        this.ticketNumber = ticketNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRoll = studentRoll;
        this.category = category;
        this.title = title;
        this.description = description;
        this.location = location;
        this.priority = priority;
        this.status = status;
        this.anonymous = anonymous;
        this.assignedDepartmentId = assignedDepartmentId;
        this.assignedDepartmentName = assignedDepartmentName;
        this.assignedStaffId = assignedStaffId;
        this.assignedStaffName = assignedStaffName;
        this.createdAt = createdAt;
        this.deadlineAt = deadlineAt;
        this.resolvedAt = resolvedAt;
        this.rejectionReason = rejectionReason;
    }

    // Getters and Setters
    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Integer getAssignedDepartmentId() {
        return assignedDepartmentId;
    }

    public void setAssignedDepartmentId(Integer assignedDepartmentId) {
        this.assignedDepartmentId = assignedDepartmentId;
    }

    public String getAssignedDepartmentName() {
        return assignedDepartmentName;
    }

    public void setAssignedDepartmentName(String assignedDepartmentName) {
        this.assignedDepartmentName = assignedDepartmentName;
    }

    public String getAssignedStaffId() {
        return assignedStaffId;
    }

    public void setAssignedStaffId(String assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }

    public String getAssignedStaffName() {
        return assignedStaffName;
    }

    public void setAssignedStaffName(String assignedStaffName) {
        this.assignedStaffName = assignedStaffName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadlineAt() {
        return deadlineAt;
    }

    public void setDeadlineAt(LocalDateTime deadlineAt) {
        this.deadlineAt = deadlineAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * Checks whether this complaint has crossed its resolution SLA deadline without being resolved.
     */
    public boolean isOverdue() {
        if (status.isTerminal() || deadlineAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(deadlineAt);
    }

    /**
     * Helper to mask student identity if submitted anonymously.
     */
    public String getDisplayAuthor(boolean isAdmin) {
        if (!anonymous) {
            return (studentName != null ? studentName : "Student") + " (" + (studentRoll != null ? studentRoll : studentId) + ")";
        }
        if (isAdmin) {
            return "[Anonymous] " + (studentName != null ? studentName : "Student") + " (" + studentRoll + ")";
        }
        return "Anonymous Student (Confidential)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complaint complaint = (Complaint) o;
        return Objects.equals(complaintId, complaint.complaintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complaintId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s, %s)", ticketNumber, title, category, priority, status);
    }
}
