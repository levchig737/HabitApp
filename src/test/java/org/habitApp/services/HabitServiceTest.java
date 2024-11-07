package org.habitApp.services;

import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitAlreadyCompletedException;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.models.Period;
import org.habitApp.repositories.impl.HabitComletionHistoryRepositoryImpl;
import org.habitApp.repositories.impl.HabitRepositoryImpl;
import org.habitApp.services.impl.HabitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HabitServiceTest {

    @Mock
    private HabitRepositoryImpl habitRepository;

    @Mock
    private HabitComletionHistoryRepositoryImpl habitComletionHistoryRepository;

    @InjectMocks
    private HabitServiceImpl habitService;

    private UserEntity testUser;
    private HabitEntity testHabit;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new UserEntity(1, "testUser@example.com", "password123", "Test User", false);
        testHabit = new HabitEntity(1, "Test Habit", "Testing", "day", LocalDate.now(), testUser.getId());
    }

    @Test
    public void getHabitById_ShouldReturnHabit_WhenUserHasAccess() throws SQLException, HabitNotFoundException, UnauthorizedAccessException {
        when(habitRepository.getHabitById(1L)).thenReturn(testHabit);

        HabitEntity habit = habitService.getHabitById(1L, testUser);

        assertEquals(testHabit, habit);
        verify(habitRepository, times(1)).getHabitById(1L);
    }

    @Test
    public void getHabitById_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit() throws SQLException {
        UserEntity otherUser = new UserEntity(2,"otherUser@example.com", "password123", "Other User", false);
        when(habitRepository.getHabitById(1)).thenReturn(testHabit);

        assertThrows(UnauthorizedAccessException.class, () -> habitService.getHabitById(1, otherUser));
    }

    @Test
    public void createHabit_ShouldCallRepositorySaveMethod() throws SQLException {
        Period frequency = Period.DAY;

        habitService.createHabit(testUser, "New Habit", "Description", frequency);

        verify(habitRepository, times(1)).createHabit(any(HabitEntity.class));
    }

    @Test
    public void deleteHabit_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit() throws SQLException {
        UserEntity otherUser = new UserEntity(2,"otherUser@example.com", "password123", "Other User", false);

        when(habitRepository.getHabitById(1)).thenReturn(testHabit);

        assertThrows(UnauthorizedAccessException.class, () -> habitService.deleteHabit(1));
    }

    @Test
    public void getAllHabits_ShouldReturnUserHabits() throws SQLException {
        when(habitRepository.getHabitsByUser(testUser)).thenReturn(List.of(testHabit));

        List<HabitEntity> habits = habitService.getAllHabits(testUser);

        assertEquals(1, habits.size());
        assertEquals(testHabit, habits.get(0));
    }

    @Test
    public void markHabitAsCompleted_ShouldThrowHabitAlreadyCompletedException_WhenCompletedToday() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(List.of(LocalDate.now()));
        when(habitRepository.getHabitById(1L)).thenReturn(testHabit);

        assertThrows(HabitAlreadyCompletedException.class, () -> habitService.markHabitAsCompleted(1L));
    }

    @Test
    public void calculateCurrentStreak_ShouldReturnStreakCount() throws SQLException {
        List<LocalDate> completionHistory = List.of(
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1),
                LocalDate.now()
        );
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(completionHistory);

        int streak = habitService.calculateCurrentStreak(testHabit);

        assertEquals(3, streak);
    }

    @Test
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
    public void generateProgressReport_ShouldReturnHabitReportDto() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(1)).thenReturn(List.of(LocalDate.now().minusDays(1)));
        when(habitRepository.getHabitById(1)).thenReturn(testHabit);

        HabitReportDto report = habitService.generateProgressReport(testHabit, Period.DAY);

        assertNotNull(report);
    }
}
