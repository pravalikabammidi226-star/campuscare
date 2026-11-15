package com.campuscare.dao;

import com.campuscare.model.Department;
import java.util.Optional;

/**
 * Data Access Object interface for Department operations.
 */
public interface DepartmentDao extends GenericDao<Department, Integer> {
    Optional<Department> findByCode(String code);
}
