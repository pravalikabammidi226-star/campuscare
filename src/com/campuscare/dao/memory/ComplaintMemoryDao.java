package com.campuscare.dao.memory;

import com.campuscare.dao.ComplaintDao;
import com.campuscare.model.Complaint;
import com.campuscare.model.enums.ComplaintCategory;
import com.campuscare.model.enums.ComplaintStatus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComplaintMemoryDao implements ComplaintDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<Complaint> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.complaints.get(id));
    }

    @Override
    public List<Complaint> findAll() {
        return storage.complaints.values().stream()
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Complaint save(Complaint entity) {
        if (entity != null && entity.getComplaintId() != null) {
            storage.complaints.put(entity.getComplaintId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(Complaint entity) {
        if (entity != null && entity.getComplaintId() != null && storage.complaints.containsKey(entity.getComplaintId())) {
            storage.complaints.put(entity.getComplaintId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return storage.complaints.remove(id) != null;
    }

    @Override
    public Optional<Complaint> findByTicketNumber(String ticketNumber) {
        if (ticketNumber == null) return Optional.empty();
        return storage.complaints.values().stream()
                .filter(c -> ticketNumber.equalsIgnoreCase(c.getTicketNumber()))
                .findFirst();
    }

    @Override
    public List<Complaint> findByStudentId(String studentId) {
        return storage.complaints.values().stream()
                .filter(c -> studentId.equals(c.getStudentId()))
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByDepartmentId(int departmentId) {
        return storage.complaints.values().stream()
                .filter(c -> c.getAssignedDepartmentId() != null && c.getAssignedDepartmentId() == departmentId)
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStaffId(String staffId) {
        return storage.complaints.values().stream()
                .filter(c -> staffId.equals(c.getAssignedStaffId()))
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return storage.complaints.values().stream()
                .filter(c -> c.getStatus() == status)
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByCategory(ComplaintCategory category) {
        return storage.complaints.values().stream()
                .filter(c -> c.getCategory() == category)
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findActiveComplaints() {
        return storage.complaints.values().stream()
                .filter(c -> !c.getStatus().isTerminal())
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findOverdueComplaints() {
        return storage.complaints.values().stream()
                .filter(Complaint::isOverdue)
                .sorted(Comparator.comparing(Complaint::getDeadlineAt))
                .collect(Collectors.toList());
    }
}
