package com.my.repository.impl;

import com.my.model.User;
import com.my.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> repository = new ConcurrentHashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Optional<User> getByEmail(String email) {
        User user = repository.get(email);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public User save(User user) {
        repository.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public boolean deleteByEmail(String email) {
        return false;
    }
}
