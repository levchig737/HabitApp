package org.habitApp.repositories;

import org.habitApp.domain.entities.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для всех репозиториев с операциями CRUD
 *
 * @param <IdType> Тип идентификатора сущности
 * @param <EntityType> Тип сущности
 */
public interface BaseRepository<IdType, EntityType> {
    /**
     * Создает новую сущность.
     *
     * @param obj Объект сущности, который нужно сохранить.
     * @return Созданная сущность с установленным идентификатором.
     */
    EntityType create(EntityType obj) throws SQLException;

    /**
     * Поиск сущности по id.
     *
     * @param id id сущности, которую нужно найти.
     * @return Optional<EntityType>
     */
    Optional<EntityType> findById(IdType id) throws SQLException;

    /**
     * Возвращает список всех сущностей.
     *
     * @return Список всех сущностей.
     */
    List<EntityType> findAll() throws SQLException;

    /**
     * Обновляет существующую сущность.
     *
     * @param obj Объект сущности с обновлёнными данными.
     * @return true, если обновление прошло успешно, иначе false.
     */
    boolean update(EntityType obj) throws SQLException;

    /**
     * Удаляет сущность по id.
     *
     * @param id id сущности
     * @return true, если удаление прошло успешно, иначе false.
     */
    boolean deleteById(IdType id) throws SQLException;

    /**
     * Преобразование строки результата запроса в объект UserEntity.
     *
     * @param resultSet Результат запроса
     * @return Объект UserEntity, соответствующий строке результата
     * @throws SQLException В случае ошибок при работе с результатом запроса
     */
    EntityType mapRowToEntity(ResultSet resultSet) throws SQLException;
}
