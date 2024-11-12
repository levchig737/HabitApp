package org.habitApp;

import org.habitApp.annotations.EnableAuditTrackExecutionTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableAuditTrackExecutionTime
@SpringBootApplication
public class HabitAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(HabitAppApplication.class, args);
    }
}