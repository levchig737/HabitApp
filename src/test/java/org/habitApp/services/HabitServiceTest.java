package org.habitApp.services;

import org.habitApp.models.Habit;
import org.habitApp.models.Period;
import org.habitApp.models.User;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitComletionHistoryRepository habitComletionHistoryRepository;

    @InjectMocks
    private HabitService habitService;

    private User user;
    private Habit habit;
    private Period period;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "user", "password", "user", true);
        habit = new Habit(UUID.randomUUID(), "Exercise", "Daily exercise", "day", LocalDate.now(), user.getId());
        period = Period.DAY;
    }

    @Test
    void testCreateHabit() throws SQLException {
        habitService.createHabit(user, habit.getName(), habit.getDescription(), period);

        verify(habitRepository, times(1)).createHabit(any(Habit.class));
    }

    @Test
    void testUpdateHabit() throws SQLException {
        UUID habitId = habit.getId();
        String newName = "Exercise Updated";
        String newDescription = "Updated daily exercise";
        Period newFrequency = Period.WEEK;

        habitService.updateHabit(habitId, newName, newDescription, newFrequency);

        verify(habitRepository, times(1)).updateHabit(eq(habitId), any(Habit.class));
    }

    @Test
    void testDeleteHabitWithCorrectUser() throws SQLException {
        when(habitRepository.getHabitById(habit.getId())).thenReturn(habit);
        doNothing().when(habitRepository).deleteHabit(habit.getId());

        habitService.deleteHabit(habit.getId(), user);

        verify(habitRepository, times(1)).deleteHabit(habit.getId());
    }



    @Test
    void testDeleteHabitWithIncorrectUser() throws SQLException {
        User otherUser = new User(UUID.randomUUID(), "other@example.com", "Other User", "test", false);
        when(habitRepository.getHabitById(habit.getId())).thenReturn(habit);

        habitService.deleteHabit(habit.getId(), otherUser);

        verify(habitRepository, times(0)).deleteHabit(habit.getId());
    }

    @Test
    void testGetAllHabitsForUser() throws SQLException {
        when(habitRepository.getHabitsByUser(user)).thenReturn(List.of(habit));

        List<Habit> habits = habitService.getAllHabits(user);

        assertNotNull(habits);
        assertEquals(1, habits.size());
        assertEquals(habit, habits.get(0));
    }

    @Test
    void testGetAllHabitsAdminWithAdminUser() throws SQLException, IllegalAccessException {
        when(habitRepository.getAllHabits()).thenReturn(List.of(habit));

        List<Habit> habits = habitService.getAllHabitsAdmin(user);

        assertNotNull(habits);
        assertEquals(1, habits.size());
    }

    @Test
    void testGetAllHabitsAdminWithNonAdminUser() {
        User nonAdminUser = new User(UUID.randomUUID(), "nonadmin@example.com", "Non Admin", "test",false);

        assertThrows(IllegalAccessException.class, () -> habitService.getAllHabitsAdmin(nonAdminUser));
    }

    @Test
    void testMarkHabitAsCompleted() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId()))
                .thenReturn(List.of(LocalDate.now().minusDays(1)));

        habitService.markHabitAsCompleted(habit);

        verify(habitComletionHistoryRepository, times(1))
                .addComletionDateByHabitIdUserIs(habit.getId(), habit.getUserId(), LocalDate.now());
    }

    @Test
    void testMarkHabitAsCompletedTwiceInOneDayThrowsException() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId()))
                .thenReturn(List.of(LocalDate.now()));

        assertThrows(IllegalArgumentException.class, () -> habitService.markHabitAsCompleted(habit));
    }

    @Test
    void testCalculateCurrentStreak() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId()))
                .thenReturn(List.of(LocalDate.now().minusDays(2), LocalDate.now().minusDays(1), LocalDate.now()));

        int streak = habitService.calculateCurrentStreak(habit);

        assertEquals(3, streak);
    }

    @Test
    void testCalculateCompletionPercentage() throws SQLException {
        when(habitComletionHistoryRepository.getCompletionHistoryForHabit(habit.getId()))
                .thenReturn(List.of(LocalDate.now().minusDays(1), LocalDate.now()));

        double percentage = habitService.calculateCompletionPercentage(habit, period);

        assertTrue(percentage > 0);
    }
}
