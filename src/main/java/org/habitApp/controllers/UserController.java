package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.habitApp.annotations.Loggable;
import org.habitApp.config.beans.CurrentUserBean;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с пользователями.
 * Обеспечивает регистрацию, вход, выход, обновление и удаление профилей пользователей.
 */
@Loggable
@Tag(name = "User Controller", description = "Операции для работы с профилями пользователей, включая регистрацию, вход, обновление и удаление.")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService; // Сервис для работы с пользователями
    private final UserMapper userMapper; // Маппер для преобразования между UserDto и UserEntity
    private final CurrentUserBean currentUserBean;  // Инжектируем сессионный бин

    /**
     * Конструктор UserController
     * @param userService userService
     * @param userMapper userMapper
     * @param currentUserBean currentUserBean
     */
    public UserController(UserService userService, UserMapper userMapper, CurrentUserBean currentUserBean) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.currentUserBean = currentUserBean;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param userDtoRegisterUpdate Данные для регистрации пользователя
     * @return Ответ с кодом состояния и сообщением
     */
    @Operation(summary = "Регистрация нового пользователя", description = "Регистрирует нового пользователя с переданными данными.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDtoRegisterUpdate userDtoRegisterUpdate) {
        try {
            userService.registerUser(userDtoRegisterUpdate);
        } catch (SQLException | UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Вход пользователя в систему.
     *
     * @param userDtoLogin Данные для входа (email и пароль)
     * @return Ответ с данными пользователя или сообщение об ошибке
     */
    @Operation(summary = "Вход пользователя", description = "Выполняет аутентификацию пользователя по его email и паролю.")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDtoLogin userDtoLogin) {
        try {
            UserDto userDto = userService.loginUser(userDtoLogin);
            if (userDto != null) {
                currentUserBean.setCurrentUser(userMapper.userDtoToUser(userDto));  // Устанавливаем текущего пользователя в сессионный бин
                return ResponseEntity.ok(userDto);
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (SQLException | InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Выход пользователя из системы.
     *
     * @return Ответ с сообщением об успешном выходе
     */
    @Operation(summary = "Выход пользователя", description = "Завершает сессию текущего пользователя.")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Очищаем данные текущего пользователя
        currentUserBean.setCurrentUser(null);
        return ResponseEntity.ok().body("Logged out successfully");
    }

    /**
     * Обновление профиля текущего пользователя.
     *
     * @param userDtoRegisterUpdate Данные для обновления профиля
     * @return Ответ с кодом состояния
     */
    @Operation(summary = "Обновление профиля", description = "Обновляет информацию профиля текущего пользователя.")
    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserDtoRegisterUpdate userDtoRegisterUpdate) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            userService.updateCurrentUserProfile(userDtoRegisterUpdate, currentUserBean.getCurrentUser());
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
    public ResponseEntity<?> deleteCurrentUser() {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            userService.deleteCurrentUser(currentUserBean.getCurrentUser());
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
    public ResponseEntity<?> getUser(@RequestParam String email) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            UserDto userDto = userService.getUser(email, currentUserBean.getCurrentUser());
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
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            List<UserDto> users = userService.getAllUsers(currentUserBean.getCurrentUser());
            return ResponseEntity.ok(users);
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Обновление профиля пользователя по email (доступно только администраторам).
     *
     * @param id ID пользователя
     * @param userDto Данные для обновления
     * @return Ответ с обновленными данными пользователя
     */
    @Operation(summary = "Обновление профиля пользователя", description = "Обновляет профиль пользователя по его ID для администратора.")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateUserProfile(@RequestParam long id, @RequestBody UserDtoRegisterUpdate userDtoRegisterUpdate) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            UserDto updatedUserDto = userService.updateUserProfile(id, userDtoRegisterUpdate, currentUserBean.getCurrentUser());
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@RequestParam long id) {
        if (!currentUserBean.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            userService.deleteUser(id, currentUserBean.getCurrentUser());
            return ResponseEntity.ok().build();
        } catch (SQLException | UnauthorizedAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
