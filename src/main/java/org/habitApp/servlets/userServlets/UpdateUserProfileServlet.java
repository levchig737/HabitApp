package org.habitApp.servlets.userServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Сервлет обновления пользователя текущим пользователем
 */
@Loggable
@WebServlet("/user/update")
public class UpdateUserProfileServlet extends HttpServlet {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UpdateUserProfileServlet() {
        this.userService = new UserService(new UserRepository());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
//            HttpSession session = req.getSession(false);
            UserEntity currentUser = (UserEntity) req.getSession().getAttribute("user");


            if (currentUser == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            UserDto userDto = objectMapper.readValue(req.getInputStream(), UserDto.class);
            userService.updateCurrentUserProfile(userDto.getName(), userDto.getPassword(), currentUser);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Profile updated successfully");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("User not found");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }
}
