package com.campuscare.dao.memory;

import com.campuscare.dao.StudentDao;
import com.campuscare.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentMemoryDao implements StudentDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<Student> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.students.get(id));
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(storage.students.values());
    }

    @Override
    public Student save(Student entity) {
        if (entity != null && entity.getId() != null) {
            storage.students.put(entity.getId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(Student entity) {
        if (entity != null && entity.getId() != null && storage.students.containsKey(entity.getId())) {
            storage.students.put(entity.getId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return storage.students.remove(id) != null;
    }

    @Override
    public Optional<Student> findByRollNumber(String rollNumber) {
        if (rollNumber == null) return Optional.empty();
        return storage.students.values().stream()
                .filter(s -> rollNumber.equalsIgnoreCase(s.getRollNumber()))
                .findFirst();
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return storage.students.values().stream()
                .filter(s -> email.equalsIgnoreCase(s.getEmail()))
                .findFirst();
    }
}
