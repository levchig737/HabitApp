package org.habitApp.controllers;

import org.habitApp.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping
    public ResponseEntity<?> getTestUser() {
        try {
            return ResponseEntity.ok(testService.sayHello());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }
}
