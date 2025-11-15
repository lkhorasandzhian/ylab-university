package ru.ylab.levon.service;

import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;

import java.util.List;

public class AuditService {

    private final AuditRepository repository;

    public AuditService(AuditRepository repository) {
        this.repository = repository;
    }

    public void log(String username, String action) {
        repository.add(new AuditRecord(username, action));
    }

    public List<AuditRecord> getAll() {
        return repository.findAll();
    }

    public List<AuditRecord> getLast(int count) {
        return repository.findLast(count);
    }

    public List<AuditRecord> getStorage() {
        return repository.getStorage();
    }
}
