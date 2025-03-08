package com.my.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    List<T> getAll();

    Optional<T> getById(Long id);

    T save(T entity);

    T update(T entity);

    boolean deleteById(Long id);
}
