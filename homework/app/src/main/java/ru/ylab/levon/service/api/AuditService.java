package ru.ylab.levon.service.api;

import ru.ylab.levon.model.AuditRecord;

import java.util.List;

public interface AuditService {
    void log(String username, String action);

    List<AuditRecord> getAll();
}
