package org.habitApp.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.habitApp.annotations.Loggable;
import org.habitApp.dto.HealthResponseDto;
import org.habitApp.services.HealthCheckService;

import java.io.IOException;

@Loggable
@WebServlet("/health")
public class HealthCheckServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final HealthCheckService healthCheckService;

    public HealthCheckServlet() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.healthCheckService = new HealthCheckService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        HealthResponseDto.HealthStatus healthStatus = healthCheckService.getHealthStatus();
        HealthResponseDto healthResponseDto = new HealthResponseDto(healthStatus);

        byte[] bytes = objectMapper.writeValueAsBytes(healthResponseDto);
        resp.getOutputStream().write(bytes);
    }
}
