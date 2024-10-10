package habitApp.services;

import habitApp.models.Habit;
import habitApp.models.User;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис для управления привычками (CRUD)
 * Позволяет создавать, редактировать, удалять и просматривать привычки пользователя.
 */
public class HabitService {
    private final Map<String, List<Habit>> habits = new HashMap<>();
    /**
     * Создание новой привычки
     * @param user пользователь, создающий привычку
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки
     */
    public void createHabit(User user, String name, String description, String frequency) {
        Habit habit = new Habit(habits.size(), name, description, frequency, user);
        habits.computeIfAbsent(user.getEmail(), k -> new ArrayList<>()).add(habit);
        System.out.println("Привычка \"" + name + "\" создана для пользователя " + user.getEmail() + ".");
    }

    /**
     * Редактирование привычки
     * @param habit привычка для редактирования
     * @param newName новое название
     * @param newDescription новое описание
     * @param newFrequency новая частота выполнения
     */
    public void updateHabit(User user, Habit habit, String newName, String newDescription, String newFrequency) {
        List<Habit> userHabits = habits.get(user.getEmail());
        if (userHabits != null && userHabits.contains(habit)) {
            habit.setName(newName);
            habit.setDescription(newDescription);
            habit.setFrequency(newFrequency);
            System.out.println("Привычка обновлена: " + habit.getName());
        } else {
            System.out.println("Привычка не найдена у пользователя " + user.getEmail());
        }
    }

    /**
     * Удаление привычки
     * @param user пользователь
     * @param habit привычка для удаления
     */
    public void deleteHabit(User user, Habit habit) {
        List<Habit> userHabits = habits.get(user.getEmail());
        if (userHabits != null && userHabits.remove(habit)) {
            System.out.println("Привычка \"" + habit.getName() + "\" была удалена.");
        } else {
            System.out.println("Привычка не найдена у пользователя " + user.getEmail());
        }
    }

    /**
     * Получение всех привычек пользователя
     * @param user текущий пользователь
     * @return список привычек пользователя
     */
    public List<Habit> getAllHabits(User user) {
        return habits.getOrDefault(user.getEmail(), new ArrayList<>());
    }

    /**
     * Получение списка всех привычек для всех пользователей
     * @param currentUser текущий пользователь
     * @return Map<String, List<Habit>> список всех привычек
     * @throws IllegalAccessException доступ для админа
     */
    public Map<String, List<Habit>> getAllHabitsAdmin(User currentUser) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }

        return habits;
    }

    /**
     * Обновление привычки
     * @param currentUser текущий пользователь
     * @param idHabit id привычки
     * @param newHabit новая привычка
     * @return Habit новый
     * @throws IllegalAccessException доступ у админа
     */
    public Habit updateHabitById(User currentUser, int idHabit, Habit newHabit)
            throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }

        Set<String> setHabits = habits.keySet();
        for ( String email : setHabits ) {
            List<Habit> habitsList = habits.get(email);
            for (Habit habit : habitsList) {
                if (habit.getId() == idHabit) {
                    habit = newHabit;
                    habit.setId(idHabit);
                    return habit;
                }
            }
        }
        return null;
    }

    /**
     * Удаление привчки
     * @param currentUser текущий пользователь
     * @param emailOwner email владельца
     * @param idHabit id привычки
     * @throws IllegalAccessException доступ у админа
     */
    public void deleteHabit(User currentUser, String emailOwner, int idHabit) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }
        List<Habit> userHabits = habits.get(emailOwner);
        for (int i=0; i < userHabits.size(); i++) {
            if (userHabits.get(i).getId() == idHabit) {
                userHabits.remove(i);
                return;

            }
        }
    }
}
