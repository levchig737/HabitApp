// package org.habitApp.repositories.impl;
package org.habitApp.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.models.Role;
import org.habitApp.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.habitApp.repositories.constants.UserSqlQueries.*;

/**
 * Репозиторий для работы с данными пользователей.
 * Содержит методы для выполнения операций с базой данных, связанных с пользователями.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource; // Источник данных для подключения к базе данных


    /**
     * Получение пользователя по email.
     *
     * @param email Email пользователя
     * @return Optional с пользователем, если найден, или пустой Optional, если нет
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public Optional<UserEntity> findByEmail(String email) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_USER_BY_EMAIL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToEntity(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Получение пользователя по ID.
     *
     * @param id ID пользователя
     * @return Optional с пользователем, если найден, или пустой Optional, если нет
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public Optional<UserEntity> findById(Long id) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_USER_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToEntity(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список всех пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public List<UserEntity> findAll() throws SQLException {
        List<UserEntity> users = new ArrayList<>();
        try (Statement statement = dataSource.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_USERS)) {
            while (resultSet.next()) {
                users.add(mapRowToEntity(resultSet));
            }
        }
        return users;
    }

    /**
     * Создание нового пользователя.
     *
     * @param user Пользователь, который должен быть зарегистрирован
     * @return Созданный пользователь с установленным идентификатором
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public UserEntity create(UserEntity user) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(REGISTER_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole().toString());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
        }
        return user;
    }

    /**
     * Обновление информации о пользователе.
     *
     * @param user Пользователь с обновленной информацией
     * @return true, если обновление прошло успешно, иначе false
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public boolean update(UserEntity user) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(UPDATE_USER)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().toString());
            statement.setString(4, user.getEmail());
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Удаление пользователя по ID.
     *
     * @param id ID пользователя, которого нужно удалить
     * @return true, если удаление прошло успешно, иначе false
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public boolean deleteById(Long id) throws SQLException {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(DELETE_USER_BY_ID)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Преобразование строки результата запроса в объект UserEntity.
     *
     * @param resultSet Результат запроса
     * @return Объект UserEntity, соответствующий строке результата
     * @throws SQLException В случае ошибок при работе с результатом запроса
     */
    @Override
    public UserEntity mapRowToEntity(ResultSet resultSet) throws SQLException {
        return new UserEntity(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("username"),
                Role.fromString(resultSet.getString("role"))
        );
    }
}
