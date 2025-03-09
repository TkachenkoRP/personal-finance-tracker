package com.my.repository.impl;

import com.my.mapper.UserMapper;
import com.my.model.User;
import com.my.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> repository = new ConcurrentHashMap<>();

    private final AtomicLong currentId = new AtomicLong(1);

    @Override
    public List<User> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        User user = repository.get(id);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(UserMapper.INSTANCE.copyUser(user));
    }

    @Override
    public User save(User user) {
        Long id = currentId.getAndIncrement();
        user.setId(id);
        repository.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteById(Long id) {
        if (getById(id).isPresent()) {
            repository.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPresentByEmail(String email) {
        return getAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<User> getByEmailAndPassword(String email, String password) {
        for (User u : getAll()) {
            if (u.getEmail().equalsIgnoreCase(email)
                    && u.getPassword().equals(password)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }
}
