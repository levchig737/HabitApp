package org.habitApp.controllers;

import org.habitApp.annotations.Loggable;
import org.habitApp.config.beans.CurrentUserBean;
import org.habitApp.domain.dto.habitDto.HabitDtoCreateUpdate;
import org.habitApp.domain.dto.habitDto.HabitDtoResponse;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.mappers.HabitMapper;
import org.habitApp.models.Period;
import org.habitApp.services.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Loggable
@RestController
@RequestMapping("/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;

    @Autowired
    private HabitMapper habitMapper;

    @Autowired
    private CurrentUserBean currentUserBean;

    // Создание новой привычки
    @PostMapping
    public ResponseEntity<?> createHabit(@RequestBody HabitDtoCreateUpdate habitDto) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            habitService.createHabit(
                    currentUserBean.getCurrentUser(),
                    habitDto.getName(),
                    habitDto.getDescription(),
                    Period.fromString(habitDto.getFrequency())
            );
            return ResponseEntity.ok().body("Habit created successfully.");
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Обновление привычки
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHabit(@PathVariable long id, @RequestBody HabitDtoCreateUpdate habitDto) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            habitService.updateHabit(
                    id,
                    habitDto.getName(),
                    habitDto.getDescription(),
                    Period.fromString(habitDto.getFrequency())
            );
            return ResponseEntity.ok().body("Habit updated successfully.");
        } catch (SQLException | HabitNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Удаление привычки
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable long id) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            habitService.deleteHabit(id, currentUserBean.getCurrentUser());
            return ResponseEntity.ok().body("Habit deleted successfully.");
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение всех привычек текущего пользователя
    @GetMapping
    public ResponseEntity<?> getAllHabits() {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            List<HabitDtoResponse> habits = habitService.getAllHabits(currentUserBean.getCurrentUser())
                    .stream().map(habitMapper::habitToHabitDtoResponse).toList();
            return ResponseEntity.ok(habits);
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение всех привычек для администратора
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllHabitsAdmin() {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            List<HabitDtoResponse> habits = habitService.getAllHabitsAdmin(currentUserBean.getCurrentUser())
                    .stream().map(habitMapper::habitToHabitDtoResponse).toList();
            return ResponseEntity.ok(habits);
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение информации о привычке по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getHabitById(@PathVariable long id) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            HabitDtoResponse habitDtoResponse = habitMapper.habitToHabitDtoResponse(
                    habitService.getHabitById(id, currentUserBean.getCurrentUser())
            );
            return ResponseEntity.ok(habitDtoResponse);
        } catch (SQLException | HabitNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Отметка привычки как выполненной
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> markHabitCompleted(@PathVariable long id) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            habitService.markHabitAsCompleted(id);
            return ResponseEntity.ok().body("Habit marked as completed.");
        } catch (SQLException | HabitNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение статистики выполнения привычки за указанный период
    @GetMapping("/{id}/report/{period}")
    public ResponseEntity<?> getHabitReport(@PathVariable long id, @PathVariable String period) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            int completionCount = habitService.calculateHabitCompletedByPeriod(
                    habitService.getHabitById(id, currentUserBean.getCurrentUser()), Period.fromString(period));
            int currentStreak = habitService.calculateCurrentStreak(habitService.getHabitById(id, currentUserBean.getCurrentUser()));

            HabitReportDto reportDto = new HabitReportDto();
            reportDto.setCompletionCount(completionCount);
            reportDto.setStreak(currentStreak);

            return ResponseEntity.ok(reportDto);
        } catch (SQLException | HabitNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Вычисление процента выполнения привычки за указанный период
    @GetMapping("/{id}/completion-percentage/{period}")
    public ResponseEntity<?> getCompletionPercentage(@PathVariable long id, @PathVariable String period) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            double completionPercentage = habitService.calculateCompletionPercentage(
                    habitService.getHabitById(id, currentUserBean.getCurrentUser()), Period.fromString(period));
            return ResponseEntity.ok(completionPercentage);
        } catch (SQLException | HabitNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Генерация отчета о прогрессе выполнения привычки
    @GetMapping("/{id}/progress-report/{period}")
    public ResponseEntity<?> generateProgressReport(@PathVariable long id, @PathVariable String period) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            HabitReportDto reportDto = habitService.generateProgressReport(
                    habitService.getHabitById(id, currentUserBean.getCurrentUser()), Period.fromString(period));
            return ResponseEntity.ok(reportDto);
        } catch (SQLException | HabitNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
