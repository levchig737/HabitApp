package org.habitApp.servlets.habitServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.habitApp.annotations.Loggable;
import org.habitApp.models.User;
import org.habitApp.repositories.HabitComletionHistoryRepository;
import org.habitApp.repositories.HabitRepository;
import org.habitApp.services.HabitService;

import java.io.IOException;
import java.util.UUID;

/**
 * Сервлет удаления привычки текущим пользователем
 */
@Loggable
@WebServlet("/habit/delete")
public class DeleteHabitCurrentUserServlet extends HttpServlet {
    private final HabitService habitService;
    private final ObjectMapper objectMapper;

    public DeleteHabitCurrentUserServlet() {
        this.habitService = new HabitService(new HabitRepository(), new HabitComletionHistoryRepository());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

            if (currentUser == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            UUID habitId = UUID.fromString(req.getParameter("habitId"));
            habitService.deleteHabit(habitId, currentUser);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Habit deleted successfully");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }
}
