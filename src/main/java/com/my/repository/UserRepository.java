package com.my.repository;

import com.my.model.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
    boolean isPresentByEmail(String email);

    Optional<User> getByEmailAndPassword(String email, String password);
}
