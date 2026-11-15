package com.campuscare.dao;

import com.campuscare.model.Student;
import java.util.Optional;

/**
 * Data Access Object interface for Student operations.
 */
public interface StudentDao extends GenericDao<Student, String> {
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByEmail(String email);
}
