package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.habitApp.services.UserService;
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
}