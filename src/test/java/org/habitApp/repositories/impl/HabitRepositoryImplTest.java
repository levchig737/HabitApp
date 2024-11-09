package org.habitApp.repositories.impl;

import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HabitRepositoryImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private HabitRepositoryImpl habitRepository;

    private HabitEntity habitEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() throws SQLException {
        lenient().when(dataSource.getConnection()).thenReturn(connection);
        habitEntity = new HabitEntity(1L, "Test Habit", "Description", "Daily", LocalDate.now(), 1L);
        userEntity = new UserEntity(1L, "test@example.com", "password123", "Test User", false);
    }

    @Test
    @DisplayName("[getHabitsByUser] Получение привычек по пользователю - Успешно")
    void testGetHabitsByUser() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(habitEntity.getId());
        when(resultSet.getString("name")).thenReturn(habitEntity.getName());
        when(resultSet.getString("description")).thenReturn(habitEntity.getDescription());
        when(resultSet.getString("frequency")).thenReturn(habitEntity.getFrequency());
        when(resultSet.getDate("created_date")).thenReturn(Date.valueOf(habitEntity.getCreatedDate()));
        when(resultSet.getLong("user_id")).thenReturn(habitEntity.getUserId());

        List<HabitEntity> habits = habitRepository.getHabitsByUser(userEntity);

        assertNotNull(habits);
        assertEquals(1, habits.size());
        assertEquals(habitEntity, habits.get(0));
    }

    @Test
    @DisplayName("[findById] Поиск по ID - Найдено")
    void testFindByIdFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(habitEntity.getId());
        when(resultSet.getString("name")).thenReturn(habitEntity.getName());
        when(resultSet.getString("description")).thenReturn(habitEntity.getDescription());
        when(resultSet.getString("frequency")).thenReturn(habitEntity.getFrequency());
        when(resultSet.getDate("created_date")).thenReturn(Date.valueOf(habitEntity.getCreatedDate()));
        when(resultSet.getLong("user_id")).thenReturn(habitEntity.getUserId());

        Optional<HabitEntity> result = habitRepository.findById(habitEntity.getId());

        assertTrue(result.isPresent());
        assertEquals(habitEntity, result.get());
    }

    @Test
    @DisplayName("[findById] Поиск по ID - Не найдено")
    void testFindByIdNotFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<HabitEntity> result = habitRepository.findById(habitEntity.getId());

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("[findAll] Получение всех привычек - Успешно")
    void testFindAll() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(habitEntity.getId());
        when(resultSet.getString("name")).thenReturn(habitEntity.getName());
        when(resultSet.getString("description")).thenReturn(habitEntity.getDescription());
        when(resultSet.getString("frequency")).thenReturn(habitEntity.getFrequency());
        when(resultSet.getDate("created_date")).thenReturn(Date.valueOf(habitEntity.getCreatedDate()));
        when(resultSet.getLong("user_id")).thenReturn(habitEntity.getUserId());

        List<HabitEntity> habits = habitRepository.findAll();

        assertNotNull(habits);
        assertEquals(1, habits.size());
        assertEquals(habitEntity, habits.get(0));
    }

    @Test
    @DisplayName("[create] Создание привычки - Успешно")
    void testCreate() throws SQLException {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(1L);

        HabitEntity createdHabit = habitRepository.create(habitEntity);

        assertNotNull(createdHabit);
        assertEquals(1L, createdHabit.getId());
    }

    @Test
    @DisplayName("[update] Обновление привычки - Успешно")
    void testUpdate() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = habitRepository.update(habitEntity);

        assertTrue(result);
    }

    @Test
    @DisplayName("[deleteById] Удаление привычки по ID - Успешно")
    void testDeleteById() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = habitRepository.deleteById(habitEntity.getId());

        assertTrue(result);
    }

    @Test
    @DisplayName("[mapRowToEntity] Преобразование строки результата в объект HabitEntity - Успешно")
    void testMapRowToEntity() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(habitEntity.getId());
        when(resultSet.getString("name")).thenReturn(habitEntity.getName());
        when(resultSet.getString("description")).thenReturn(habitEntity.getDescription());
        when(resultSet.getString("frequency")).thenReturn(habitEntity.getFrequency());
        when(resultSet.getDate("created_date")).thenReturn(Date.valueOf(habitEntity.getCreatedDate()));
        when(resultSet.getLong("user_id")).thenReturn(habitEntity.getUserId());

        HabitEntity result = habitRepository.mapRowToEntity(resultSet);

        assertNotNull(result);
        assertEquals(habitEntity, result);
    }
}
