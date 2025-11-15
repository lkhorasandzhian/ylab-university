package ru.ylab.levon.repository.api;

import ru.ylab.levon.model.AuditRecord;

import java.util.List;

public interface AuditRepository {

    void add(AuditRecord record);

    List<AuditRecord> findAll();

    List<AuditRecord> findLast(int count);

    List<AuditRecord> getStorage();
}
