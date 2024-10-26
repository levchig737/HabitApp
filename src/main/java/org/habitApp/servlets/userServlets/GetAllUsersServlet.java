package org.habitApp.servlets.userServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.habitApp.annotations.Loggable;
import org.habitApp.dto.habitDto.HabitDto;
import org.habitApp.dto.habitDto.HabitMapper;
import org.habitApp.dto.userDto.UserDto;
import org.habitApp.dto.userDto.UserMapper;
import org.habitApp.models.Habit;
import org.habitApp.models.User;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.UserService;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Сервлет получения списка всех пользователей админом
 */
@Loggable
@WebServlet("/admin/users")
public class GetAllUsersServlet extends HttpServlet {
    private UserService userService;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    public GetAllUsersServlet() {
        this.userService = new UserService(new UserRepository());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User currentUser = (User) req.getSession().getAttribute("user");

            resp.setContentType("application/json");
            List<User> users = userService.getAllUsers(currentUser);
            List<UserDto> userDtos = new ArrayList<>();
            for (User user : users) {
                userDtos.add(userMapper.userToUserDto(user));
            }

            byte[] bytes = objectMapper.writeValueAsBytes(userDtos);
            resp.getOutputStream().write(bytes);

        } catch (IllegalAccessException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("Access denied: " + e.getMessage());

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Database error: " + e.getMessage());
        }
    }
}

