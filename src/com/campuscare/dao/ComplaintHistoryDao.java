package com.campuscare.dao;

import com.campuscare.model.ComplaintHistory;
import java.util.List;

/**
 * Data Access Object interface for Complaint History audit trail.
 */
public interface ComplaintHistoryDao extends GenericDao<ComplaintHistory, Integer> {
    List<ComplaintHistory> findByComplaintId(String complaintId);
}
