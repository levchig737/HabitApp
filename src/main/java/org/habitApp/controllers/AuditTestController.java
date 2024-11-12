package org.habitApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для тестирования аспекта аудита.
 */
@Tag(name = "AuditTestController", description = "Контроллер для проверки работы аудита AuditLoggingAspect ")
@RestController
@RequestMapping("/audit-test")
public class AuditTestController {

    /**
     * Метод без задержки, не должен вызывать предупреждений.
     * @return ответ с сообщением
     */
    @Operation(summary = "Быстрый метод", description = "Быстрый ответ от сервера")
    @GetMapping("/fast")
    public ResponseEntity<String> fastMethod() {
        return ResponseEntity.ok("This is a fast method");
    }

    /**
     * Метод с задержкой, должен вызвать предупреждение, если время выполнения превышает порог.
     * @return ответ с сообщением
     * @throws InterruptedException исключение при прерывании
     */
    @Operation(summary = "Медленный метод", description = "Медленный ответ от сервера с задержкой в 1500 мс")
    @GetMapping("/slow")
    public ResponseEntity<String> slowMethod() throws InterruptedException {
        // Искусственная задержка в 1500 мс, превышающая порог времени
        Thread.sleep(1500);
        return ResponseEntity.ok("This is a slow method");
    }
}
