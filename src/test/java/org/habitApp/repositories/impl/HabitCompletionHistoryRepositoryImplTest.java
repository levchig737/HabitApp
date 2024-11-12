package org.habitApp.repositories.impl;

import org.habitApp.domain.entities.HabitCompletionHistoryEntity;
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
import static org.habitApp.repositories.constants.HabitCompletionHistorySqlQueries.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitCompletionHistoryRepositoryImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private HabitCompletionHistoryRepositoryImpl repository;

    private HabitCompletionHistoryEntity habitCompletionHistoryEntity;

    @BeforeEach
    void setUp() throws SQLException {
        lenient().when(dataSource.getConnection()).thenReturn(connection);
        habitCompletionHistoryEntity = new HabitCompletionHistoryEntity(1L, 1L, 1L, LocalDate.now());
    }

    @Test
    @DisplayName("[getCompletionHistoryForHabit] Должен вернуть историю выполнения для конкретного ID привычки")
    void testGetCompletionHistoryForHabit() throws SQLException {
        long habitId = 1L;
        when(connection.prepareStatement(GET_COMPLETION_HISTORY_FOR_HABIT)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getDate("completion_date")).thenReturn(Date.valueOf(LocalDate.now()));

        List<LocalDate> completionHistory = repository.getCompletionHistoryForHabit(habitId);

        assertEquals(1, completionHistory.size());
        verify(preparedStatement).setLong(1, habitId);
    }

    @Test
    @DisplayName("[addCompletionDateByHabitIdUserId] Должен добавить дату выполнения по ID привычки и ID пользователя")
    void testAddCompletionDateByHabitIdUserId() throws SQLException {
        long habitId = 1L;
        long userId = 1L;
        LocalDate completionDate = LocalDate.now();

        when(connection.prepareStatement(ADD_COMPLETION_DATE_BY_HABIT_ID_USER_ID)).thenReturn(preparedStatement);

        repository.addCompletionDateByHabitIdUserId(habitId, userId, completionDate);

        verify(preparedStatement).setLong(eq(1), anyLong());
        verify(preparedStatement).setLong(2, habitId);
        verify(preparedStatement).setLong(3, userId);
        verify(preparedStatement).setDate(4, Date.valueOf(completionDate));
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("[create] Должен создать новую запись в истории выполнения привычки")
    void testCreate() throws SQLException {
        when(connection.prepareStatement(CREATE_COMPLETION_HISTORY, Statement.RETURN_GENERATED_KEYS)).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(10L);

        HabitCompletionHistoryEntity result = repository.create(habitCompletionHistoryEntity);

        assertNotNull(result.getId());
        assertEquals(10L, result.getId());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("[findById] Должен найти запись истории выполнения по ID")
    void testFindById() throws SQLException {
        long id = 10L;

        when(connection.prepareStatement(GET_COMPLETION_HISTORY_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(id);
        when(resultSet.getLong("habit_id")).thenReturn(1L);
        when(resultSet.getLong("user_id")).thenReturn(1L);
        when(resultSet.getDate("completion_date")).thenReturn(Date.valueOf(LocalDate.now()));

        Optional<HabitCompletionHistoryEntity> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    @DisplayName("[findAll] Должен вернуть все записи истории выполнения привычек")
    void testFindAll() throws SQLException {
        when(connection.prepareStatement(GET_ALL_COMPLETION_HISTORY)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getLong("habit_id")).thenReturn(1L);
        when(resultSet.getLong("user_id")).thenReturn(1L);
        when(resultSet.getDate("completion_date")).thenReturn(Date.valueOf(LocalDate.now()));

        List<HabitCompletionHistoryEntity> historyList = repository.findAll();

        assertEquals(1, historyList.size());
        assertEquals(1L, historyList.get(0).getId());
    }

    @Test
    @DisplayName("[update] Должен обновить запись истории выполнения привычки")
    void testUpdate() throws SQLException {

        when(connection.prepareStatement(UPDATE_COMPLETION_HISTORY)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = repository.update(habitCompletionHistoryEntity);

        assertTrue(result);
        verify(preparedStatement).setLong(1, habitCompletionHistoryEntity.getHabitId());
        verify(preparedStatement).setLong(2, habitCompletionHistoryEntity.getUserId());
        verify(preparedStatement).setDate(3, Date.valueOf(habitCompletionHistoryEntity.getCompletionDate()));
        verify(preparedStatement).setLong(4, habitCompletionHistoryEntity.getId());
    }

    @Test
    @DisplayName("[deleteById] Должен удалить запись истории выполнения привычки по ID")
    void testDeleteById() throws SQLException {
        long id = 1L;

        when(connection.prepareStatement(DELETE_COMPLETION_HISTORY_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = repository.deleteById(id);

        assertTrue(result);
        verify(preparedStatement).setLong(1, id);
    }

    @Test
    @DisplayName("[mapRowToEntity] Должен сопоставить строку ResultSet с HabitCompletionHistoryEntity")
    void testMapRowToEntity() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getLong("habit_id")).thenReturn(1L);
        when(resultSet.getLong("user_id")).thenReturn(1L);
        when(resultSet.getDate("completion_date")).thenReturn(Date.valueOf(LocalDate.now()));

        HabitCompletionHistoryEntity entity = repository.mapRowToEntity(resultSet);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(1L, entity.getHabitId());
        assertEquals(1L, entity.getUserId());
        assertEquals(LocalDate.now(), entity.getCompletionDate());
    }
}
