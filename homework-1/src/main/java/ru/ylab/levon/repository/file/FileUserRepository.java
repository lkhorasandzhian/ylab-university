package ru.ylab.levon.repository.file;

import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class FileUserRepository implements UserRepository {

    private final Map<String, User> users;

    public FileUserRepository(Map<String, User> initialData) {
        this.users = new HashMap<>(initialData);
    }

    @Override
    public boolean save(User user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }

    @Override
    public Map<String, User> getStorage() {
        return users;
    }
}
