package ru.ylab.levon.service;

import java.util.ArrayList;
import java.util.List;

import ru.ylab.levon.model.AuditRecord;


public class AuditService {
    private final List<AuditRecord> logs = new ArrayList<>();

    public void log(String username, String action) {
        logs.add(new AuditRecord(username, action));
    }

    public List<AuditRecord> getAllLogs() {
        return logs;
    }
}
