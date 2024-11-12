package org.habitApp.repositories;

import org.habitApp.domain.entities.UserEntity;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository extends BaseRepository<Long, UserEntity> {

    /**
     * Поиск пользователя по email.
     *
     * @param email Email пользователя
     * @return Optional с пользователем, если найден, или пустой Optional, если нет
     */
    Optional<UserEntity> findByEmail(String email) throws SQLException;
}
