package com.campuscare.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object interface defining standard CRUD operations.
 * Demonstrates Generics, Abstraction, and Interface design in Core Java.
 *
 * @param <T> The entity type
 * @param <ID> The primary key type
 */
public interface GenericDao<T, ID> {

    /**
     * Finds an entity by its primary key.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities in persistence.
     */
    List<T> findAll();

    /**
     * Persists a new entity.
     */
    T save(T entity);

    /**
     * Updates an existing entity.
     */
    boolean update(T entity);

    /**
     * Deletes an entity by its ID.
     */
    boolean deleteById(ID id);
}
