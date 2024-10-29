package org.habitApp.servlets.habitServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.habitDto.HabitDto;
import org.habitApp.mappers.HabitMapper;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.habitApp.services.HabitService;
import org.mapstruct.factory.Mappers;

import java.io.IOException;


/**
 * Сервлет получения streak привычки текущим пользователем
 */
@Loggable
@WebServlet("/habit/streak")
public class HabitStreakServlet extends HttpServlet {
    private final HabitService habitService;
    private final ObjectMapper objectMapper;
    private final HabitMapper habitMapper;

    public HabitStreakServlet() {
        this.habitService = new HabitService(new HabitRepository(), new HabitComletionHistoryRepository());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.habitMapper = Mappers.getMapper(HabitMapper.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            UserEntity currentUser = (session != null) ? (UserEntity) session.getAttribute("currentUser") : null;

            if (currentUser == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            HabitDto habitDto = new ObjectMapper().readValue(req.getInputStream(), HabitDto.class);

            int streak =  habitService.calculateCurrentStreak(habitMapper.habitDtoToHabit(habitDto));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");

            byte[] bytes = objectMapper.writeValueAsBytes(streak);
            resp.getOutputStream().write(bytes);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }
}
