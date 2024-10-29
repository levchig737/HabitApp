package org.habitApp.servlets.userServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.UserService;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Сервлет регистрации пользователя текущим пользователем
 */
@Loggable
@WebServlet("/register")
public class UserRegistrationServlet extends HttpServlet {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserRegistrationServlet() {
        this.userService = new UserService(new UserRepository());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDto userDto = objectMapper.readValue(req.getInputStream(), UserDto.class);

            userService.registerUser(userDto.getEmail(), userDto.getPassword(), userDto.getName());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("User registered successfully");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("User already exists");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }
}
