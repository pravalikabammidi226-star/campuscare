package com.campuscare.dao.memory;

import com.campuscare.dao.StaffDao;
import com.campuscare.model.DepartmentStaff;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaffMemoryDao implements StaffDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<DepartmentStaff> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.staff.get(id));
    }

    @Override
    public List<DepartmentStaff> findAll() {
        return new ArrayList<>(storage.staff.values());
    }

    @Override
    public DepartmentStaff save(DepartmentStaff entity) {
        if (entity != null && entity.getId() != null) {
            storage.staff.put(entity.getId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(DepartmentStaff entity) {
        if (entity != null && entity.getId() != null && storage.staff.containsKey(entity.getId())) {
            storage.staff.put(entity.getId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return storage.staff.remove(id) != null;
    }

    @Override
    public Optional<DepartmentStaff> findByEmployeeCode(String employeeCode) {
        if (employeeCode == null) return Optional.empty();
        return storage.staff.values().stream()
                .filter(s -> employeeCode.equalsIgnoreCase(s.getEmployeeCode()))
                .findFirst();
    }

    @Override
    public Optional<DepartmentStaff> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.staff.values().stream()
                .filter(s -> email.equalsIgnoreCase(s.getEmail()))
                .findFirst();
    }

    @Override
    public List<DepartmentStaff> findByDepartmentId(int departmentId) {
        return storage.staff.values().stream()
                .filter(s -> s.getDepartmentId() == departmentId)
                .collect(Collectors.toList());
    }
}
