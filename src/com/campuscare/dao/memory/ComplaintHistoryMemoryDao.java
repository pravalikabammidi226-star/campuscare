package com.campuscare.dao.memory;

import com.campuscare.dao.ComplaintHistoryDao;
import com.campuscare.model.ComplaintHistory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComplaintHistoryMemoryDao implements ComplaintHistoryDao {
    private final InMemoryStorage storage = InMemoryStorage.getInstance();

    @Override
    public Optional<ComplaintHistory> findById(Integer id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.complaintHistories.get(id));
    }

    @Override
    public List<ComplaintHistory> findAll() {
        return new ArrayList<>(storage.complaintHistories.values());
    }

    @Override
    public ComplaintHistory save(ComplaintHistory entity) {
        if (entity != null) {
            if (entity.getHistoryId() <= 0) {
                entity.setHistoryId(storage.historyIdGen.incrementAndGet());
            }
            storage.complaintHistories.put(entity.getHistoryId(), entity);
        }
        return entity;
    }

    @Override
    public boolean update(ComplaintHistory entity) {
        if (entity != null && storage.complaintHistories.containsKey(entity.getHistoryId())) {
            storage.complaintHistories.put(entity.getHistoryId(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        return storage.complaintHistories.remove(id) != null;
    }

    @Override
    public List<ComplaintHistory> findByComplaintId(String complaintId) {
        return storage.complaintHistories.values().stream()
                .filter(h -> complaintId.equals(h.getComplaintId()))
                .sorted(Comparator.comparing(ComplaintHistory::getTimestamp))
                .collect(Collectors.toList());
    }
}
