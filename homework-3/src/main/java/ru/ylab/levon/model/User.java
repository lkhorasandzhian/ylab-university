package ru.ylab.levon.model;

import lombok.Getter;
import lombok.ToString;
import lombok.NonNull;
import lombok.AllArgsConstructor;

@Getter
@ToString(exclude = "password")
@AllArgsConstructor
public class User {
    private Long id;
    private final @NonNull String username;
    private final @NonNull String password;
    private final @NonNull Role role;

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Существующий ID не может быть изменён");
        }
        this.id = id;
    }

    public boolean checkPassword(@NonNull String input) {
        return input.equals(password);
    }
}
