package ru.ylab.levon.repository.file;

import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileAuditRepository implements AuditRepository {

    private final List<AuditRecord> logs;

    public FileAuditRepository(List<AuditRecord> initialData) {
        this.logs = new ArrayList<>(initialData);
    }

    @Override
    public void add(AuditRecord record) {
        logs.add(record);
    }

    @Override
    public List<AuditRecord> findAll() {
        return Collections.unmodifiableList(logs);
    }

    @Override
    public List<AuditRecord> findLast(int count) {
        int from = Math.max(0, logs.size() - count);
        return List.copyOf(logs.subList(from, logs.size()));
    }

    @Override
    public List<AuditRecord> getStorage() {
        return logs;
    }
}
