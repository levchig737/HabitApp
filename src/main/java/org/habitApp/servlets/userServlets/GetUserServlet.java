package org.habitApp.servlets.userServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.habitApp.annotations.Loggable;
import org.habitApp.dto.userDto.UserDto;
import org.habitApp.dto.userDto.UserMapper;
import org.habitApp.models.User;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.UserService;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Сервлет полчения пользователя админом
 */
@Loggable
@WebServlet("/admin/user")
public class GetUserServlet extends HttpServlet {
    private UserService userService;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    public GetUserServlet() {
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

            UserDto userDto = new ObjectMapper().readValue(req.getInputStream(), UserDto.class);
            User user = userService.getUser(userDto.getEmail(), currentUser);

            byte[] bytes = objectMapper.writeValueAsBytes(userMapper.userToUserDto(user));
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



