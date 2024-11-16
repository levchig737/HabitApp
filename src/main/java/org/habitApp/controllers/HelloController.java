package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.habitApp.annotations.RequiresAuthorization;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@Tag(name = "HelloController", description = "Контроллер для приветственного сообщения")
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class HelloController {

    private final UserService userService;

    @GetMapping("/hello")
    @Operation(summary = "Приветственное сообщение", description = "Возвращает приветственное сообщение.")
    public String sayHello() {
        return "Hello, World!";
    }

    /**
     * Проверяет авторизацию пользователя
     *
     * @return Ответ с данными авторизированного пользователя
     */
    @Operation(summary = "Тест авторизации пользователя", description = "Проверяет авторизацию пользователя.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/test")
//    @RequiresAuthorization
    public ResponseEntity<?> testAuth() {
        try {
            return ResponseEntity.ok().body(userService.testAuth());
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
//        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
    }

}