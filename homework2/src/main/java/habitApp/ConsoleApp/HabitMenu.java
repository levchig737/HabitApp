package habitApp.ConsoleApp;

import habitApp.models.Habit;
import habitApp.models.User;
import habitApp.services.HabitService;
import habitApp.services.HabitTrackingService;

import java.util.List;
import java.util.Scanner;

/**
 * Класс отображения меню привычек
 */
public class HabitMenu implements Menu {
    private final HabitService habitService;
    private final HabitTrackingService habitTrackingService;
    private final User currentUser;

    /**
     * Конструктор HabitMenu
     * @param habitService сервис для управления привычками
     * @param habitTrackingService сервис для отслеживания выполнения привычек
     * @param currentUser текущий пользователь
     */
    public HabitMenu(HabitService habitService, HabitTrackingService habitTrackingService, User currentUser) {
        this.habitService = habitService;
        this.habitTrackingService = habitTrackingService;
        this.currentUser = currentUser;
    }

    /**
     * Выбор действия в терминале
     * @param scanner поток in
     */
    @Override
    public void show(Scanner scanner) {
        while (true) {
            System.out.println("""
                    МЕНЮ ПРИВЫЧЕК
                    1. Создать привычку
                    2. Редактировать привычку
                    3. Удалить привычку
                    4. Просмотреть привычки
                    5. Отметить выполнение привычки
                    6. Показать статистику
                    7. Назад""");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> createHabit(scanner);
                case 2 -> editHabit(scanner);
                case 3 -> deleteHabit(scanner);
                case 4 -> viewHabits(scanner);
                case 5 -> markHabitAsCompleted(scanner);
                case 6 -> showHabitStatistics(scanner);
                case 7 -> {
                    return;
                }
                default -> System.out.println("Нет такого варианта. Попробуйте еще раз.");
            }
        }
    }

    /**
     * Создание привычки
     * @param scanner поток in
     */
    private void createHabit(Scanner scanner) {
        System.out.print("Введите название привычки: ");
        String name = scanner.next();
        System.out.print("Введите описание привычки: ");
        String description = scanner.next();
        System.out.print("Введите частоту (ежедневно/еженедельно): ");
        String frequency = scanner.next();

        habitService.createHabit(currentUser, name, description, frequency);
    }

    /**
     * Редактирование привычки
     * @param scanner поток in
     */
    private void editHabit(Scanner scanner) {
        viewHabits(scanner);
        System.out.print("Выберите номер привычки для редактирования: ");
        int index = scanner.nextInt();
        List<Habit> habits = habitService.getAllHabits(currentUser);
        if (index < 1 || index > habits.size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);

        System.out.print("Введите новое название: ");
        String newName = scanner.next();
        System.out.print("Введите новое описание: ");
        String newDescription = scanner.next();
        System.out.print("Введите новую частоту (ежедневно/еженедельно): ");
        String newFrequency = scanner.next();

        habitService.updateHabit(currentUser, habit, newName, newDescription, newFrequency);
    }

    /**
     * Удаление привычки
     * @param scanner поток in
     */
    private void deleteHabit(Scanner scanner) {
        viewHabits(scanner);
        System.out.print("Выберите номер привычки для удаления: ");
        int index = scanner.nextInt();
        List<Habit> habits = habitService.getAllHabits(currentUser);
        if (index < 1 || index > habits.size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);
        habitService.deleteHabit(currentUser, habit);
    }

    /**
     * Посмотреть список привычек
     * @param scanner поток in
     */
    private void viewHabits(Scanner scanner) {
        List<Habit> habits = habitService.getAllHabits(currentUser);
        if (habits.isEmpty()) {
            System.out.println("У вас нет привычек.");
        } else {
            System.out.println("Ваши привычки:");
            for (int i = 0; i < habits.size(); i++) {
                System.out.println((i + 1) + ". " + habits.get(i).toString() + ",");
            }
        }
    }

    /**
     * Отметить выполнение привычки
     * @param scanner поток in
     */
    private void markHabitAsCompleted(Scanner scanner) {
        viewHabits(scanner);
        System.out.print("Выберите номер привычки для отметки выполнения: ");
        int index = scanner.nextInt();
        List<Habit> habits = habitService.getAllHabits(currentUser);
        if (index < 1 || index > habits.size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);
        try {
            habitTrackingService.markHabitAsCompleted(habit);
            System.out.println("Привычка \"" + habit.getName() + "\" отмечена как выполненная.");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Отображение статистики привычек
     * @param scanner поток in
     */
    private void showHabitStatistics(Scanner scanner) {
        viewHabits(scanner);
        System.out.print("Выберите номер привычки для просмотра статистики: ");
        int index = scanner.nextInt();
        System.out.print("Выберите промежуток привычки(day/week/month): ");
        String period = scanner.next();

        List<Habit> habits = habitService.getAllHabits(currentUser);
        if (index < 1 || index > habits.size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);
        try {
            System.out.println(habitTrackingService.generateProgressReport(habit, period));
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
