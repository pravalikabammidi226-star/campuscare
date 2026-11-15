package com.campuscare.dao;

import com.campuscare.model.Complaint;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.ComplaintStatus;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Complaints.
 */
public interface ComplaintDao extends GenericDao<Complaint, String> {
    Optional<Complaint> findByTicketNumber(String ticketNumber);
    List<Complaint> findByStudentId(String studentId);
    List<Complaint> findByDepartmentId(int departmentId);
    List<Complaint> findByStaffId(String staffId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByCategory(ComplaintCategory category);
    List<Complaint> findActiveComplaints();
    List<Complaint> findOverdueComplaints();
}
