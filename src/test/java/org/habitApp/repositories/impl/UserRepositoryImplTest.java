package org.habitApp.repositories.impl;

import org.habitApp.domain.entities.UserEntity;
import org.habitApp.repositories.impl.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.habitApp.repositories.constants.UserSqlQueries.GET_ALL_USERS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() throws SQLException {
        lenient().when(dataSource.getConnection()).thenReturn(connection);
        userEntity = new UserEntity(1L, "test@example.com", "password", "Test User", false);
    }

    @Test
    @DisplayName("[findByEmail] Поиск пользователя по email - Найден")
    void testFindByEmailFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(userEntity.getId());
        when(resultSet.getString("email")).thenReturn(userEntity.getEmail());
        when(resultSet.getString("password")).thenReturn(userEntity.getPassword());
        when(resultSet.getString("name")).thenReturn(userEntity.getName());
        when(resultSet.getBoolean("is_admin")).thenReturn(userEntity.isFlagAdmin());

        Optional<UserEntity> result = userRepository.findByEmail(userEntity.getEmail());

        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
    }

    @Test
    @DisplayName("[findByEmail] Поиск пользователя по email - Не найден")
    void testFindByEmailNotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<UserEntity> result = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("[findById] Поиск пользователя по ID - Найден")
    void testFindByIdFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(userEntity.getId());
        when(resultSet.getString("email")).thenReturn(userEntity.getEmail());
        when(resultSet.getString("password")).thenReturn(userEntity.getPassword());
        when(resultSet.getString("name")).thenReturn(userEntity.getName());
        when(resultSet.getBoolean("is_admin")).thenReturn(userEntity.isFlagAdmin());

        Optional<UserEntity> result = userRepository.findById(userEntity.getId());

        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
    }

    @Test
    @DisplayName("[findById] Поиск пользователя по ID - Не найден")
    void testFindByIdNotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<UserEntity> result = userRepository.findById(2L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("[findAll] Получение всех пользователей - Успешно")
    void testFindAll() throws SQLException {
        when(connection.createStatement()).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(userEntity.getId());
        when(resultSet.getString("email")).thenReturn(userEntity.getEmail());
        when(resultSet.getString("password")).thenReturn(userEntity.getPassword());
        when(resultSet.getString("name")).thenReturn(userEntity.getName());
        when(resultSet.getBoolean("is_admin")).thenReturn(userEntity.isFlagAdmin());

        List<UserEntity> users = userRepository.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userEntity, users.get(0));
    }

    @Test
    @DisplayName("[create] Создание пользователя - Успешно")
    void testCreate() throws SQLException {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(1L);

        UserEntity createdUser = userRepository.create(userEntity);

        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());
    }

    @Test
    @DisplayName("[update] Обновление пользователя - Успешно")
    void testUpdate() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userRepository.update(userEntity);

        assertTrue(result);
    }

    @Test
    @DisplayName("[deleteById] Удаление пользователя по ID - Успешно")
    void testDeleteById() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userRepository.deleteById(userEntity.getId());

        assertTrue(result);
    }

    @Test
    @DisplayName("[mapRowToEntity] Преобразование строки результата в объект UserEntity - Успешно")
    void testMapRowToEntity() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(userEntity.getId());
        when(resultSet.getString("email")).thenReturn(userEntity.getEmail());
        when(resultSet.getString("password")).thenReturn(userEntity.getPassword());
        when(resultSet.getString("name")).thenReturn(userEntity.getName());
        when(resultSet.getBoolean("is_admin")).thenReturn(userEntity.isFlagAdmin());

        UserEntity result = userRepository.mapRowToEntity(resultSet);

        assertNotNull(result);
        assertEquals(userEntity, result);
    }
}
