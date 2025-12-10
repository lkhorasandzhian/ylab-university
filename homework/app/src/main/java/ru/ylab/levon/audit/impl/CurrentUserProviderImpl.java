package ru.ylab.levon.audit.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ylab.levon.audit.api.CurrentUserProvider;
import ru.ylab.levon.service.api.UserService;

@Component
@RequiredArgsConstructor
public class CurrentUserProviderImpl implements CurrentUserProvider {
    private final UserService userService;

    @Override
    public String getCurrentUsername() {
        var user = userService.getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
}
