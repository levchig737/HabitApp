package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с пользователями.
 * Обеспечивает, обновление и удаление профилей пользователей.
 */
@Tag(name = "User Controller", description = "Операции для работы с профилями пользователей: обновление и удаление.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // Сервис для работы с пользователями
    private final UserMapper userMapper; // Маппер для преобразования между UserDto и UserEntity

    /**
     * Обновление профиля текущего пользователя.
     *
     * @param userDtoRegisterUpdate Данные для обновления профиля
     * @param currentUser текущий пользователь
     * @return Ответ с кодом состояния
     */
    @Operation(summary = "Обновление профиля", description = "Обновляет информацию профиля текущего пользователя.")
    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserDtoRegisterUpdate userDtoRegisterUpdate,
                                                      @AuthenticationPrincipal UserEntity currentUser) {
        try {
            userService.updateCurrentUserProfile(userDtoRegisterUpdate, currentUser);
        } catch (SQLException | UserNotFoundException | UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление профиля текущего пользователя.
     *
     * @return Ответ с кодом состояния
     */
    @Operation(summary = "Удаление профиля", description = "Удаляет текущий профиль пользователя.")
    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserEntity currentUser) {
        try {
            userService.deleteCurrentUser(currentUser);
        } catch (SQLException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Получение информации о пользователе по email (доступно только администраторам).
     *
     * @param email Email пользователя
     * @return Ответ с данными пользователя
     */
    @Operation(summary = "Получение пользователя по email", description = "Возвращает информацию о пользователе для администратора.")
    @GetMapping("/admin/{email}")
    public ResponseEntity<?> getUser(@PathVariable("email") String email) {
        try {
            UserDto userDto = userService.getUser(email);
            return ResponseEntity.ok(userDto);
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Получение списка всех пользователей (доступно только администраторам).
     *
     * @return Ответ со списком пользователей
     */
    @Operation(summary = "Получение списка пользователей", description = "Возвращает список всех пользователей для администратора.")
    @GetMapping("/admin")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Обновление профиля пользователя по email (доступно только администраторам).
     *
     * @param id ID пользователя
     * @param userDtoRegisterUpdate Данные для обновления
     * @return Ответ с обновленными данными пользователя
     */
    @Operation(summary = "Обновление профиля пользователя", description = "Обновляет профиль пользователя по его ID для администратора.")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable("id") long id, @RequestBody UserDtoRegisterUpdate userDtoRegisterUpdate) {

        try {
            UserDto updatedUserDto = userService.updateUserProfile(id, userDtoRegisterUpdate);
            return ResponseEntity.ok(updatedUserDto);
        } catch (SQLException | UnauthorizedAccessException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Удаление пользователя по ID (доступно только администраторам).
     *
     * @param id ID пользователя
     * @return Ответ с кодом состояния
     */
    @Operation(summary = "Удаление пользователя по ID", description = "Удаляет профиль пользователя по его ID для администратора.")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
