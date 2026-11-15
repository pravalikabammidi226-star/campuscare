package com.campuscare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model class for student feedback submitted on resolved complaints.
 */
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private int feedbackId;
    private String complaintId;
    private String studentId;
    private int rating; // 1 to 5 stars
    private String comments;
    private LocalDateTime createdAt;

    public Feedback() {
        this.createdAt = LocalDateTime.now();
    }

    public Feedback(String complaintId, String studentId, int rating, String comments) {
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = LocalDateTime.now();
    }

    public Feedback(int feedbackId, String complaintId, String studentId, int rating, String comments, LocalDateTime createdAt) {
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Feedback[Rating: %d/5, Comments: %s]", rating, comments);
    }
}
