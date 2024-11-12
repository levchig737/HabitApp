package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "HelloController", description = "Контроллер для приветственного сообщения")
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Приветственное сообщение", description = "Возвращает приветственное сообщение.")
    public String sayHello() {
        return "Hello, World!";
    }
}