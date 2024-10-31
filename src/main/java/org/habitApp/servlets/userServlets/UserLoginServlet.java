package org.habitApp.servlets.userServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.habitApp.annotations.Loggable;
import org.habitApp.dto.userDto.UserDto;
import org.habitApp.dto.userDto.UserMapper;
import org.habitApp.models.User;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.UserService;
import org.mapstruct.factory.Mappers;

import java.io.IOException;


/**
 * Сервлет авторизации пользователя текущим пользователем
 */
@Loggable
@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserLoginServlet() {
        this.userService = new UserService(new UserRepository());
        this.userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDto userDto = new ObjectMapper().readValue(req.getInputStream(), UserDto.class);

            User user = userService.loginUser(userDto.getEmail(), userDto.getPassword());

            if (user != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("User logged in successfully");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Invalid credentials");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }
}

