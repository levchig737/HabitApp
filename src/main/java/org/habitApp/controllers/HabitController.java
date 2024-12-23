package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.habitApp.domain.dto.habitDto.HabitDtoCreateUpdate;
import org.habitApp.domain.dto.habitDto.HabitDtoResponse;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.mappers.HabitMapper;
import org.habitApp.models.Period;
import org.habitApp.services.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Tag(name = "HabitController", description = "Контроллер для управления привычками пользователя.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
public class HabitController {
    private final HabitService habitService;
    private final HabitMapper habitMapper;

    /**
     * Создает новую привычку для текущего аутентифицированного пользователя.
     *
     * @param habitDto данные для создания привычки (название, описание, частота).
     * @param currentUser текущий пользователь
     * @return ResponseEntity с сообщением об успешном создании или с сообщением об ошибке.
     */
    @Operation(summary = "Создание привычки", description = "Создает новую привычку для текущего аутентифицированного пользователя.")
    @PostMapping
    public ResponseEntity<?> createHabit(@RequestBody HabitDtoCreateUpdate habitDto, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            habitService.createHabit(
                    habitDto.getName(),
                    habitDto.getDescription(),
                    Period.fromString(habitDto.getFrequency()),
                    currentUser
            );
            return ResponseEntity.ok().body("Habit created successfully.");
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Обновляет существующую привычку, указанную по ID, для текущего аутентифицированного пользователя.
     *
     * @param id идентификатор привычки.
     * @param habitDto данные для обновления привычки (название, описание, частота)
     * @param currentUser текущий пользователь
     * @return ResponseEntity с сообщением об успешном обновлении или с сообщением об ошибке.
     */
    @Operation(summary = "Обновление привычки", description = "Обновляет существующую привычку, указанную по ID, для текущего аутентифицированного пользователя.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHabit(@PathVariable("id") long id, @RequestBody HabitDtoCreateUpdate habitDto, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            habitService.updateHabit(
                    id,
                    habitDto.getName(),
                    habitDto.getDescription(),
                    Period.fromString(habitDto.getFrequency()),
                    currentUser
            );
            return ResponseEntity.ok().body("Habit updated successfully.");
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Удаляет привычку, указанную по ID, для текущего аутентифицированного пользователя.
     *
     * @param id идентификатор привычки
     * @param currentUser текущий пользователь
     * @return ResponseEntity с сообщением об успешном удалении или с сообщением об ошибке.
     */
    @Operation(summary = "Удаление привычки", description = "Удаляет привычку, указанную по ID, для текущего аутентифицированного пользователя.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable("id") long id, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            habitService.deleteHabit(id, currentUser);
            return ResponseEntity.ok().body("Habit deleted successfully.");
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Возвращает список всех привычек текущего аутентифицированного пользователя.
     *
     * @param currentUser текущий пользователь
     * @return ResponseEntity со списком всех привычек или с сообщением об ошибке.
     */
    @Operation(summary = "Получение всех привычек пользователя авторизованного", description = "Возвращает список всех привычек текущего аутентифицированного пользователя.")
    @GetMapping
    public ResponseEntity<?> getAllHabits(@AuthenticationPrincipal UserEntity currentUser) {
        try {
            List<HabitDtoResponse> habits = habitService.getAllHabits(currentUser)
                    .stream().map(habitMapper::habitToHabitDtoResponse).toList();
            return ResponseEntity.ok(habits);
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Возвращает список всех привычек для администратора.
     *
     * @return ResponseEntity со списком всех привычек или с сообщением об ошибке.
     */
    @Operation(summary = "Получение всех привычек для администратора", description = "Возвращает список всех привычек для администратора.")
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllHabitsAdmin() {
        try {
            List<HabitDtoResponse> habits = habitService.getAllHabitsAdmin()
                    .stream().map(habitMapper::habitToHabitDtoResponse).toList();
            return ResponseEntity.ok(habits);
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Возвращает привычку по ее идентификатору для текущего аутентифицированного пользователя.
     *
     * @param id идентификатор привычки
     * @param currentUser текущий пользователь
     * @return ResponseEntity с данными о привычке или с сообщением об ошибке.
     */
    @Operation(summary = "Получение привычки по ID", description = "Возвращает привычку по ее идентификатору для текущего аутентифицированного пользователя.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getHabitById(@PathVariable("id") long id, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            HabitDtoResponse habitDtoResponse = habitMapper.habitToHabitDtoResponse(
                    habitService.getHabitById(id, currentUser)
            );
            return ResponseEntity.ok(habitDtoResponse);
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Отмечает привычку как выполненную для текущего аутентифицированного пользователя.
     *
     * @param id идентификатор привычки.
     * @return ResponseEntity с сообщением об успешном завершении или с сообщением об ошибке.
     */
    @Operation(summary = "Отметка привычки как выполненной", description = "Отмечает привычку как выполненную для текущего аутентифицированного пользователя.")
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> markHabitCompleted(@PathVariable("id") long id) {
        try {
            habitService.markHabitAsCompleted(id);
            return ResponseEntity.ok().body("Habit marked as completed.");
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Возвращает отчет о привычке за указанный период (количество выполнений и текущий прогресс).
     *
     * @param id идентификатор привычки.
     * @param period период для отчета (например, "день", "неделя")
     * @param currentUser текущий пользователь
     * @return ResponseEntity с отчетом или с сообщением об ошибке.
     */
    @Operation(summary = "Получение отчета о привычке", description = "Возвращает отчет о привычке за указанный период (количество выполнений и текущий прогресс).")
    @GetMapping("/{id}/report/{period}")
    public ResponseEntity<?> getHabitReport(@PathVariable("id") long id, @PathVariable("period") String period, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            int completionCount = habitService.calculateHabitCompletedByPeriod(
                    habitService.getHabitById(id, currentUser), Period.fromString(period));
            int currentStreak = habitService.calculateCurrentStreak(habitService.getHabitById(id, currentUser));

            HabitReportDto reportDto = new HabitReportDto();
            reportDto.setCompletionCount(completionCount);
            reportDto.setStreak(currentStreak);

            return ResponseEntity.ok(reportDto);
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Возвращает процент выполнения привычки за указанный период.
     *
     * @param id идентификатор привычки.
     * @param period период для расчета процента выполнения (например, "месяц", "год").
     * @param currentUser текущий пользователь
     * @return ResponseEntity с процентом выполнения или с сообщением об ошибке.
     */
    @Operation(summary = "Получение процента выполнения привычки", description = "Возвращает процент выполнения привычки за указанный период.")
    @GetMapping("/{id}/completion-percentage/{period}")
    public ResponseEntity<?> getCompletionPercentage(@PathVariable("id") long id, @PathVariable("period") String period, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            double completionPercentage = habitService.calculateCompletionPercentage(
                    habitService.getHabitById(id, currentUser), Period.fromString(period));
            return ResponseEntity.ok(completionPercentage);
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Генерирует отчет о прогрессе выполнения привычки за указанный период.
     *
     * @param id идентификатор привычки.
     * @param period период для отчета о прогрессе (например, "месяц", "год")
     * @param currentUser текущий пользователь
     * @return ResponseEntity с отчетом о прогрессе или с сообщением об ошибке.
     */
    @Operation(summary = "Генерация отчета о прогрессе привычки", description = "Генерирует отчет о прогрессе выполнения привычки за указанный период.")
    @GetMapping("/{id}/progress-report/{period}")
    public ResponseEntity<?> generateProgressReport(@PathVariable("id") long id, @PathVariable("period") String period, @AuthenticationPrincipal UserEntity currentUser) {
        try {
            HabitReportDto reportDto = habitService.generateProgressReport(
                    habitService.getHabitById(id, currentUser), Period.fromString(period));
            return ResponseEntity.ok(reportDto);
        } catch (SQLException | HabitNotFoundException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
