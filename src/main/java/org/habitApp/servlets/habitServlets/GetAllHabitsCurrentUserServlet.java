package org.habitApp.servlets.habitServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.habitDto.HabitDto;
import org.habitApp.mappers.HabitMapper;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.habitApp.services.HabitService;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервлет получения всех привычек текущим пользователем
 */
@Loggable
@WebServlet("/habit/all")
public class GetAllHabitsCurrentUserServlet extends HttpServlet {
    private final HabitService habitService;
    private final ObjectMapper objectMapper;
    private final HabitMapper habitMapper;

    public GetAllHabitsCurrentUserServlet() {
        this.habitService = new HabitService(new HabitRepository(), new HabitComletionHistoryRepository());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.habitMapper = Mappers.getMapper(HabitMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            UserEntity currentUser = (session != null) ? (UserEntity) session.getAttribute("currentUser") : null;

            if (currentUser == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("Not Auth");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");

            List<HabitEntity> habits = habitService.getAllHabits(currentUser);
            List<HabitDto> habitDtos = new ArrayList<>();

            for (HabitEntity habit : habits) {
                habitDtos.add(habitMapper.habitToHabitDto(habit));
            }

            byte[] bytes = objectMapper.writeValueAsBytes(habitDtos);
            resp.getOutputStream().write(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }
}


