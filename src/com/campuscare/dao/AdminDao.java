package com.campuscare.dao;

import com.campuscare.model.Admin;
import java.util.Optional;

/**
 * Data Access Object interface for Administrator operations.
 */
public interface AdminDao extends GenericDao<Admin, String> {
    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmail(String email);
}
