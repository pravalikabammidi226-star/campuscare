package com.campuscare.dao;

import com.campuscare.model.DepartmentStaff;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Department Staff operations.
 */
public interface StaffDao extends GenericDao<DepartmentStaff, String> {
    Optional<DepartmentStaff> findByEmployeeCode(String employeeCode);
    Optional<DepartmentStaff> findByEmail(String email);
    List<DepartmentStaff> findByDepartmentId(int departmentId);
}
