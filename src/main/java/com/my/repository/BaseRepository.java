package com.my.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для репозиториев, предоставляющий основные операции
 * для работы с сущностями.
 *
 * @param <T> тип сущности, с которой будет работать репозиторий
 */
public interface BaseRepository<T> {
    /**
     * Получение списка всех сущностей.
     *
     * @return список всех сущностей
     */
    List<T> getAll() throws SQLException;

    /**
     * Получение сущности по её идентификатору.
     *
     * @param id идентификатор сущности
     * @return объект Optional, содержащий найденную сущность, или пустой,
     * если сущность с указанным идентификатором не найдена
     */
    Optional<T> getById(Long id) throws SQLException;

    /**
     * Сохранение новой сущности.
     *
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(T entity) throws SQLException;

    /**
     * Обновление существующей сущности.
     *
     * @param entity сущность с обновленной информацией
     * @return обновленная сущность
     */
    T update(T entity) throws SQLException;

    /**
     * Удаление сущности по её идентификатору.
     *
     * @param id идентификатор сущности
     * @return true, если сущность была успешно удалена, иначе false
     */
    boolean deleteById(Long id) throws SQLException;
}
