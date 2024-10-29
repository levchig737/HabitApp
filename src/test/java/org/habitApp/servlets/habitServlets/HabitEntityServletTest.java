//package org.habitApp.servlets.habitServlets;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class HabitEntityServletTest {
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private HttpSession session;
//
//    @InjectMocks
//    private CreateHabitCurrentUserServlet createHabitServlet;
//
//    @InjectMocks
//    private DeleteHabitCurrentUserServlet deleteHabitServlet;
//
//    @InjectMocks
//    private GetAllHabitsCurrentUserServlet getAllHabitsServlet;
//
//    @InjectMocks
//    private HabitCompletionServlet habitCompletionServlet;
//
//    @InjectMocks
//    private HabitReportServlet habitReportServlet;
//
//    @InjectMocks
//    private HabitStreakServlet habitStreakServlet;
//
//    @InjectMocks
//    private HabitUpdateCurrentUserServlet habitUpdateServlet;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private StringWriter responseWriter;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        MockitoAnnotations.openMocks(this);
//        lenient().when(request.getSession(false)).thenReturn(session);
//        responseWriter = new StringWriter();
//        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
//    }
//
//    // Тест для CreateHabitCurrentUserServlet
//    @Test
//    public void testCreateHabitUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        createHabitServlet.doPost(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    // Тест для DeleteHabitCurrentUserServlet
//    @Test
//    public void testDeleteHabitUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        deleteHabitServlet.doDelete(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    // Тест для GetAllHabitsCurrentUserServlet
//    @Test
//    public void testGetAllHabitsUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        getAllHabitsServlet.doGet(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    // Тест для HabitCompletionServlet
//    @Test
//    public void testCompleteHabitUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        habitCompletionServlet.doPost(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    // Тест для HabitReportServlet
//    @Test
//    public void testGetHabitReportUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        habitReportServlet.doPost(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    // Тест для HabitStreakServlet
//    @Test
//    public void testGetHabitStreakUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        habitStreakServlet.doPost(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//
//    // Тест для HabitUpdateCurrentUserServlet
//    @Test
//    public void testUpdateHabitUnauthorized() throws Exception {
//        when(request.getSession(false)).thenReturn(null);
//
//        habitUpdateServlet.doPost(request, response);
//
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
//}
