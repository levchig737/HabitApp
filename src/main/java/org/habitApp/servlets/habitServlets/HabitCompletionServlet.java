package org.habitApp.servlets.habitServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.habitApp.annotations.Loggable;
import org.habitApp.dto.habitDto.HabitDto;
import org.habitApp.dto.habitDto.HabitMapper;
import org.habitApp.models.Period;
import org.habitApp.models.User;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.habitApp.services.HabitService;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Сервлет выполнения привычки текущим пользователем
 */
@Loggable
@WebServlet("/habit/complete")
public class HabitCompletionServlet extends HttpServlet {
    private final HabitService habitService;
    private final ObjectMapper objectMapper;
    private final HabitMapper habitMapper;

    public HabitCompletionServlet() {
        this.habitService = new HabitService(new HabitRepository(), new HabitComletionHistoryRepository());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.habitMapper = Mappers.getMapper(HabitMapper.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

            if (currentUser == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            HabitDto habitDto = objectMapper.readValue(req.getInputStream(), HabitDto.class);

            habitService.markHabitAsCompleted(habitMapper.habitDtoToHabit(habitDto));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Habit add complete successfully");
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

