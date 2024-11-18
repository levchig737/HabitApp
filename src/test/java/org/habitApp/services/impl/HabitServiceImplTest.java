package org.habitApp.services.impl;

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
    private HabitCompletionHistoryRepository habitCompletionHistoryRepository;

    @Mock
    private HabitRepository habitRepository;

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
    @DisplayName("getHabitById_ShouldReturnHabit_WhenUserHasAccess")
    public void getHabitById_ShouldReturnHabit_WhenUserHasAccess() throws SQLException, HabitNotFoundException, UnauthorizedAccessException {
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        HabitEntity habit = habitService.getHabitById(1L, testUser);

        assertEquals(testHabit, habit);
        verify(habitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getHabitById_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit")
    public void getHabitById_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit() throws SQLException {
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        UserEntity anotherUser = new UserEntity(2, "otherUser@example.com", "password123", "Other User", Role.ROLE_USER);

        assertThrows(UnauthorizedAccessException.class, () -> habitService.getHabitById(1L, anotherUser));
    }

    @Test
    @DisplayName("createHabit_ShouldCallRepositorySaveMethod")
    public void createHabit_ShouldCallRepositorySaveMethod() throws SQLException {
        Period frequency = Period.DAY;

        habitService.createHabit("New Habit", "Description", frequency, testUser);

        verify(habitRepository, times(1)).create(any(HabitEntity.class));
    }

    @Test
    @DisplayName("deleteHabit_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit")
    public void deleteHabit_ShouldThrowUnauthorizedAccessException_WhenUserDoesNotOwnHabit() throws SQLException {
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        UserEntity anotherUser = new UserEntity(2, "otherUser@example.com", "password123", "Other User", Role.ROLE_USER);

        assertThrows(UnauthorizedAccessException.class, () -> habitService.deleteHabit(1L, anotherUser));
    }

    @Test
    @DisplayName("getAllHabits_ShouldReturnUserHabits")
    public void getAllHabits_ShouldReturnUserHabits() throws SQLException {
        when(habitRepository.getHabitsByUser(testUser)).thenReturn(List.of(testHabit));

        List<HabitEntity> habits = habitService.getAllHabits(testUser);

        assertEquals(1, habits.size());
        assertEquals(testHabit, habits.get(0));
        verify(habitRepository, times(1)).getHabitsByUser(testUser);
    }

    @Test
    @DisplayName("markHabitAsCompleted_ShouldThrowHabitAlreadyCompletedException_WhenCompletedToday")
    public void markHabitAsCompleted_ShouldThrowHabitAlreadyCompletedException_WhenCompletedToday() throws SQLException {
        when(habitCompletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(List.of(LocalDate.now()));
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        assertThrows(HabitAlreadyCompletedException.class, () -> habitService.markHabitAsCompleted(1L));
    }

    @Test
    @DisplayName("calculateCurrentStreak_ShouldReturnStreakCount")
    public void calculateCurrentStreak_ShouldReturnStreakCount() throws SQLException {
        List<LocalDate> completionHistory = List.of(
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1),
                LocalDate.now()
        );

        when(habitCompletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(completionHistory);

        int streak = habitService.calculateCurrentStreak(testHabit);

        assertEquals(3, streak);
    }

    @Test
    @DisplayName("calculateCompletionPercentage_ShouldReturnPercentage")
    public void calculateCompletionPercentage_ShouldReturnPercentage() throws SQLException {
        List<LocalDate> completionHistory = List.of(
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(1)
        );
        when(habitCompletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(completionHistory);

        double percentage = habitService.calculateCompletionPercentage(testHabit, Period.WEEK);

        assertTrue(percentage > 0);
    }

    @Test
    @DisplayName("generateProgressReport_ShouldReturnHabitReportDto")
    public void generateProgressReport_ShouldReturnHabitReportDto() throws SQLException {
        when(habitCompletionHistoryRepository.getCompletionHistoryForHabit(1L)).thenReturn(List.of(LocalDate.now().minusDays(1)));

        HabitReportDto report = habitService.generateProgressReport(testHabit, Period.DAY);

        assertNotNull(report);
    }
}
