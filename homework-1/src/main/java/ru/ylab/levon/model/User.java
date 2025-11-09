package ru.ylab.levon.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Getter
@ToString(exclude = "password")
@AllArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final @NonNull String username;
    @Getter(AccessLevel.NONE)
    private final @NonNull String password;
    private final @NonNull Role role;

    public boolean checkPassword(@NonNull String input) {
        return input.equals(password);
    }
}
