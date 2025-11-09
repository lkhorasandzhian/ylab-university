package ru.ylab.levon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import ru.ylab.levon.model.AuditRecord;


public class AuditService {
    private final List<AuditRecord> logs;

    public AuditService() {
        this.logs = new ArrayList<>();
    }

    public AuditService(List<AuditRecord> auditRecords) {
        this.logs = new ArrayList<>(auditRecords);
    }

    public void log(String username, String action) {
        logs.add(new AuditRecord(username, action));
    }

    public List<AuditRecord> getAll() {
        return Collections.unmodifiableList(logs);
    }

    public List<AuditRecord> getLast(int count) {
        int fromIndex = Math.max(0, logs.size() - count);
        return List.copyOf(logs.subList(fromIndex, logs.size()));
    }

    public void clear() {
        logs.clear();
    }
}
