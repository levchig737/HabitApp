package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;


/**
 * Контроллер для обработки запросов, связанных с пользователями.
 * Обеспечивает регистрацию, вход, выход, обновление и удаление профилей пользователей.
 */
@Tag(name = "AuthController", description = "Операции для авторизации пользователей, регистрации.")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
            authService.registerUser(userDtoRegisterUpdate);
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
    @Operation(summary = "Авторизация пользователя", description = "Авторизирует  пользователя с переданными данными.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDtoLogin userDtoLogin) {
        try {
            return ResponseEntity.ok(authService.loginUser(userDtoLogin));

        } catch (SQLException | InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Проверяет авторизацию пользователя
     *
     * @return Ответ об авторизации пользователя.
     */
    @Operation(summary = "Тест авторизации пользователя", description = "Проверяет авторизацию пользователя.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/test")
    public ResponseEntity<?> testAuth() {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();

        if (currentUser != null) {
            return ResponseEntity.ok(currentUser.toString());
        }

        return ResponseEntity.status(401).body("Unauthorized");
    }
}
