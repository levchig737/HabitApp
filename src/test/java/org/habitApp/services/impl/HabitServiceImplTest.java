package org.habitApp.services.impl;

import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitAlreadyCompletedException;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.models.Period;
import org.habitApp.models.Role;
import org.habitApp.repositories.HabitCompletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HabitServiceImplTest {

    @Mock
    private HabitCompletionHistoryRepository habitComletionHistoryRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private AuthInMemoryContext authInMemoryContext;

    @InjectMocks
    private HabitServiceImpl habitService;

    private UserEntity testUser;
    private HabitEntity testHabit;

    @BeforeEach
    public void setUp() {
        testUser = new UserEntity(1, "testUser@example.com", "password123", "Test User", Role.ROLE_USER);
        testHabit = new HabitEntity(1, "Test Habit", "Testing", "day", LocalDate.now(), testUser.getId());
    }

    @Test
    @DisplayName("[getHabitById_ShouldReturnHabit_WhenUserHasAccess] Возвращает привычку по ID, если у пользователя есть доступ")
    public void getHabitById_ShouldReturnHabit_WhenUserHasAccess() throws SQLException, HabitNotFoundException, UnauthorizedAccessException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
            when(authInMemoryContext.getAuthentication()).thenReturn(testUser);
            HabitEntity habit = habitService.getHabitById(1L);

            assertEquals(testHabit, habit);
            verify(habitRepository, times(1)).findById(1L);
        }
    }

    @Test
    @DisplayName("[getHabitById_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit] Выбрасывает UnauthorizedAccessException, если пользователь не является владельцем привычки")
    public void getHabitById_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit() throws SQLException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
            when(authInMemoryContext.getAuthentication()).thenReturn(new UserEntity(2, "otherUser@example.com", "password123", "Other User", Role.ROLE_USER));

            assertThrows(UnauthorizedAccessException.class, () -> habitService.getHabitById(1L));
        }
    }

    @Test
    @DisplayName("[createHabit_ShouldCallRepositorySaveMethod] Создает новую привычку и вызывает метод сохранения в репозитории")
    public void createHabit_ShouldCallRepositorySaveMethod() throws SQLException {
        Period frequency = Period.DAY;
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(testUser);

            habitService.createHabit("New Habit", "Description", frequency);
            verify(habitRepository, times(1)).create(any(HabitEntity.class));
        }
    }

    @Test
    @DisplayName("[deleteHabit_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit] Выбрасывает UnauthorizedAccessException при попытке удалить привычку, если пользователь не является владельцем")
    public void deleteHabit_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit() throws SQLException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
            when(authInMemoryContext.getAuthentication()).thenReturn(new UserEntity(2, "otherUser@example.com", "password123", "Other User", Role.ROLE_USER));

            assertThrows(UnauthorizedAccessException.class, () -> habitService.deleteHabit(1L));
        }
    }

    @Test
    @DisplayName("[getAllHabits_ShouldReturnUserHabits] Возвращает все привычки пользователя")
    public void getAllHabits_ShouldReturnUserHabits() throws SQLException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(testUser);
            when(habitRepository.getHabitsByUser(testUser)).thenReturn(List.of(testHabit));

            List<HabitEntity> habits = habitService.getAllHabits();
            assertEquals(1, habits.size());
            assertEquals(testHabit, habits.get(0));
        }
    }

    @Test
    @DisplayName("[markHabitAsCompleted_ShouldThrowHabitAlreadyCompletedException_WhenCompletedToday] Выбрасывает HabitAlreadyCompletedException, если привычка уже выполнена сегодня")
    public void markHabitAsCompleted_ShouldThrowHabitAlreadyCompletedException_WhenCompletedToday() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(List.of(LocalDate.now()));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        assertThrows(HabitAlreadyCompletedException.class, () -> habitService.markHabitAsCompleted(1L));
    }

    @Test
    @DisplayName("[calculateCurrentStreak_ShouldReturnStreakCount] Возвращает количество дней подряд, если пользователь выполняет привычку ежедневно")
    public void calculateCurrentStreak_ShouldReturnStreakCount() throws SQLException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(testUser);
            List<LocalDate> completionHistory = List.of(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(1),
                    LocalDate.now()
            );

            when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(completionHistory);

            int streak = habitService.calculateCurrentStreak(testHabit);
            assertEquals(3, streak);
        }
    }

    @Test
    @DisplayName("[calculateCompletionPercentage_ShouldReturnPercentage] Возвращает процент выполнения привычки за период")
    public void calculateCompletionPercentage_ShouldReturnPercentage() throws SQLException {
        List<LocalDate> completionHistory = List.of(
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1)
        );
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(completionHistory);

        double percentage = habitService.calculateCompletionPercentage(testHabit, Period.WEEK);
        assertTrue(percentage > 0);
    }

    @Test
    @DisplayName("[generateProgressReport_ShouldReturnHabitReportDto] Генерирует отчет о прогрессе выполнения привычки")
    public void generateProgressReport_ShouldReturnHabitReportDto() throws SQLException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            when(authInMemoryContext.getAuthentication()).thenReturn(testUser);

            when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(List.of(LocalDate.now().minusDays(1)));

            HabitReportDto report = habitService.generateProgressReport(testHabit, Period.DAY);
            assertNotNull(report);
        }
    }
}
