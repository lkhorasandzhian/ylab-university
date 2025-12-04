package ru.ylab.levon.config;

import ru.ylab.levon.service.AuditService;
import ru.ylab.levon.service.CatalogService;
import ru.ylab.levon.service.UserService;

public record AppContext(
        UserService userService,
        CatalogService catalogService,
        AuditService auditService
) {}
