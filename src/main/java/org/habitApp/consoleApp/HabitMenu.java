package org.habitApp.consoleApp;

import org.habitApp.models.Habit;
import org.habitApp.models.Period;
import org.habitApp.models.User;
import org.habitApp.services.HabitService;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Класс отображения меню привычек
 */
public class HabitMenu implements Menu {
    private final HabitService habitService;
    private final User currentUser;

    /**
     * Конструктор HabitMenu
     *
     * @param habitService                    сервис для управления привычками
     * @param currentUser                     текущий пользователь
     */
    public HabitMenu(HabitService habitService, User currentUser) {
        this.habitService = habitService;
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
        System.out.print("Введите частоту (day/week/month): ");
        String frequency_str = scanner.next();

        try {
            habitService.createHabit(currentUser, name, description, Period.fromString(frequency_str));
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Редактирование привычки
     * @param scanner поток in
     */
    private void editHabit(Scanner scanner) {
        viewHabits(scanner);
        System.out.print("Выберите номер привычки для редактирования: ");
        int index = scanner.nextInt();
        List<Habit> habits = null;
        try {
            habits = habitService.getAllHabits(currentUser);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (index < 1 || index > Objects.requireNonNull(habits).size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);

        System.out.print("Введите новое название: ");
        String newName = scanner.next();
        System.out.print("Введите новое описание: ");
        String newDescription = scanner.next();
        System.out.print("Введите новую частоту (day/week/month): ");
        String newFrequency = scanner.next();

        try {
            habitService.updateHabit(habit, newName, newDescription, Period.fromString(newFrequency));
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Удаление привычки
     * @param scanner поток in
     */
    private void deleteHabit(Scanner scanner) {
        viewHabits(scanner);
        System.out.print("Выберите номер привычки для удаления: ");
        int index = scanner.nextInt();
        List<Habit> habits = null;
        try {
            habits = habitService.getAllHabits(currentUser);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (index < 1 || index > Objects.requireNonNull(habits).size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);
        try {
            habitService.deleteHabit(habit);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Посмотреть список привычек
     * @param scanner поток in
     */
    private void viewHabits(Scanner scanner) {
        List<Habit> habits = null;
        try {
            habits = habitService.getAllHabits(currentUser);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (habits != null && habits.isEmpty()) {
            System.out.println("У вас нет привычек.");
        } else {
            System.out.println("Ваши привычки:");
            for (int i = 0; i < Objects.requireNonNull(habits).size(); i++) {
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
        List<Habit> habits = null;
        try {
            habits = habitService.getAllHabits(currentUser);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (index < 1 || index > habits.size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);
        try {
            habitService.markHabitAsCompleted(habit);
            System.out.println("Привычка \"" + habit.getName() + "\" отмечена как выполненная.");
        }
        catch (IllegalArgumentException | SQLException e) {
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
        String period_str = scanner.next();

        List<Habit> habits = null;
        try {
            habits = habitService.getAllHabits(currentUser);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (index < 1 || index > Objects.requireNonNull(habits).size()) {
            System.out.println("Неверный выбор.");
            return;
        }

        Habit habit = habits.get(index - 1);
        try {
            System.out.println(habitService.generateProgressReport(habit, Period.fromString(period_str)));
        }
        catch (IllegalArgumentException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
