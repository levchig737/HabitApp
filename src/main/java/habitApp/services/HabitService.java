package habitApp.services;

import habitApp.models.Habit;
import habitApp.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления привычками (CRUD)
 * Позволяет создавать, редактировать, удалять и просматривать привычки пользователя.
 */
public class HabitService {
    private final List<Habit> habits = new ArrayList<>();

    /**
     * Создание новой привычки
     * @param user пользователь, создающий привычку
     * @param name название привычки
     * @param description описание привычки
     * @param frequency частота выполнения привычки
     */
    public void createHabit(User user, String name, String description, String frequency) {
        Habit habit = new Habit(name, description, frequency);
        habits.add(habit);
        System.out.println("Привычка \"" + name + "\" создана.");
    }

    /**
     * Редактирование привычки
     * @param habit привычка для редактирования
     * @param newName новое название
     * @param newDescription новое описание
     * @param newFrequency новая частота выполнения
     */
    public void updateHabit(Habit habit, String newName, String newDescription, String newFrequency) {
        habit.setName(newName);
        habit.setDescription(newDescription);
        habit.setFrequency(newFrequency);
        System.out.println("Привычка обновлена: " + habit.getName());
    }

    /**
     * Удаление привычки
     * @param habit привычка для удаления
     */
    public void deleteHabit(Habit habit) {
        habits.remove(habit);
        System.out.println("Привычка \"" + habit.getName() + "\" была удалена.");
    }

    /**
     * Получение всех привычек пользователя
     * @return список привычек
     */
    public List<Habit> getAllHabits() {
        return new ArrayList<>(habits);
    }

    /**
     * Фильтрация привычек по дате создания
     * @param date дата фильтрации
     * @return отфильтрованный список привычек
     */
    public List<Habit> filterHabitsByDate(LocalDate date) {
        List<Habit> filtered = new ArrayList<>();
        for (Habit habit : habits) {
            if (habit.getCreatedDate().isEqual(date)) {
                filtered.add(habit);
            }
        }
        return filtered;
    }

    /**
     * Получение habit по index
     * @param currentUser текущий пользователь
     * @param index индекс habit
     * @return Habit
     * @throws IllegalAccessException Только админ
     */
    public Habit getHabitByIndex(int index, User currentUser) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }

        return habits.get(index);
    }

    /**
     * Получение списка всех привычек
     * @param currentUser текущий пользователь
     * @return List<Habit>
     * @throws IllegalAccessException Доступ только у админа
     */
    public List<Habit> getAllHabits(User currentUser) throws IllegalAccessException {
        if (!currentUser.isAdmin()) {
            throw new IllegalAccessException("User is not admin");
        }

        return habits;
    }
}

