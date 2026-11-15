package com.campuscare.dao;

import com.campuscare.model.Feedback;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for resolution Feedback.
 */
public interface FeedbackDao extends GenericDao<Feedback, Integer> {
    Optional<Feedback> findByComplaintId(String complaintId);
    List<Feedback> findByStudentId(String studentId);
}
