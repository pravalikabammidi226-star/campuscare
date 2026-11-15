package com.campuscare.dao.memory;

import com.campuscare.dao.AdminDao;
import com.campuscare.model.Admin;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminMemoryDao implements AdminDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<Admin> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.admins.get(id));
    }

    @Override
    public List<Admin> findAll() {
        return new ArrayList<>(storage.admins.values());
    }

    @Override
    public Admin save(Admin entity) {
        if (entity != null && entity.getId() != null) {
            storage.admins.put(entity.getId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(Admin entity) {
        if (entity != null && entity.getId() != null && storage.admins.containsKey(entity.getId())) {
            storage.admins.put(entity.getId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return storage.admins.remove(id) != null;
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return storage.admins.values().stream()
                .filter(a -> username.equalsIgnoreCase(a.getUsername()))
                .findFirst();
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.admins.values().stream()
                .filter(a -> email.equalsIgnoreCase(a.getEmail()))
                .findFirst();
    }
}
