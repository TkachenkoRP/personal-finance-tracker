package com.my.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    List<T> getAll();

    Optional<T> getByEmail(String email);

    T save(T entity);

    T update(T entity);

    boolean deleteByEmail(String email);
}
