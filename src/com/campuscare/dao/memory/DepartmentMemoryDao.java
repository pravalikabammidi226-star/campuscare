package com.campuscare.dao.memory;

import com.campuscare.dao.DepartmentDao;
import com.campuscare.model.Department;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentMemoryDao implements DepartmentDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<Department> findById(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.departments.get(id));
    }

    @Override
    public List<Department> findAll() {
        return new ArrayList<>(storage.departments.values());
    }

    @Override
    public Department save(Department entity) {
        if (entity != null) {
            storage.departments.put(entity.getDepartmentId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(Department entity) {
        if (entity != null && storage.departments.containsKey(entity.getDepartmentId())) {
            storage.departments.put(entity.getDepartmentId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return storage.departments.remove(id) != null;
    }

    @Override
    public Optional<Department> findByCode(String code) {
        if (code == null) return Optional.empty();
        return storage.departments.values().stream()
                .filter(d -> code.equalsIgnoreCase(d.getCode()))
                .findFirst();
    }
}
