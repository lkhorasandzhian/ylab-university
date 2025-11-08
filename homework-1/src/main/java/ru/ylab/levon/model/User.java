package ru.ylab.levon.model;

import lombok.*;

@Getter
@ToString(exclude = "password")
@AllArgsConstructor
public class User {
    private final String username;
    @Getter(AccessLevel.NONE)
    private final String password;
    private final Role role;

    public boolean checkPassword(@NonNull String input) {
        return input.equals(password);
    }
}
