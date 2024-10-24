package org.habitApp.services;

import org.habitApp.annotations.Loggable;
import org.habitApp.dto.enums.HealthStatus;

@Loggable
public class HealthCheckService {
    public HealthStatus getHealthStatus() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return HealthStatus.UP;
    }
}
