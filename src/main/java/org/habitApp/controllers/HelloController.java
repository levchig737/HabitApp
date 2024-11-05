package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Приветственное сообщение", description = "Возвращает приветственное сообщение.")
    public String sayHello() {
        return "Hello, World!";
    }
}