package org.habitApp.servlets.userServlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.habitApp.dto.userDto.UserDto;
import org.habitApp.models.User;
import org.habitApp.services.UserService;
import org.habitApp.servlets.ServletInputStreamAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServletsTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private DeleteCurrentUserServlet deleteCurrentUserServlet;

    @InjectMocks
    private DeleteUserServlet deleteUserServlet;

    @InjectMocks
    private GetAllUsersServlet getAllUsersServlet;

    @InjectMocks
    private GetUserServlet getUserServlet;

    @InjectMocks
    private UnloginUserServlet unloginUserServlet;

    @InjectMocks
    private UpdateUserProfileServlet updateUserProfileServlet;

    @InjectMocks
    private UserLoginServlet userLoginServlet;

    @InjectMocks
    private UserRegistrationServlet userRegistrationServlet;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ByteArrayOutputStream byteArrayOutputStream;
    private ServletOutputStream servletOutputStream;
    private User adminUser;
    private UserDto userDto;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        byteArrayOutputStream = new ByteArrayOutputStream();
        servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) {
                byteArrayOutputStream.write(b);
            }
        };

        lenient().when(response.getOutputStream()).thenReturn(servletOutputStream);

        adminUser = new User(UUID.randomUUID(), "admin@example.com", "password", "Admin", true);
        userDto = new UserDto(UUID.randomUUID(), "user@example.com", "password", "User", false);
    }

    @Test
    public void testDeleteCurrentUserServletSuccess() throws Exception {
        User currentUser = new User(UUID.randomUUID(), "test@example.com", "password", "Test User", false);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(currentUser);

        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream, true);
        when(response.getWriter()).thenReturn(printWriter);

        deleteCurrentUserServlet.doDelete(request, response);

//        verify(userService).deleteCurrentUser(currentUser);
//        assertFalse(byteArrayOutputStream.toString().isEmpty());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDeleteUserServletAsAdmin() throws Exception {
        // Настройка сессии для администратора
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);

        // Убедитесь, что userDto инициализирован корректно
        byte[] input = objectMapper.writeValueAsBytes(userDto);
        when(request.getInputStream()).thenReturn(new ServletInputStreamAdapter(input));

        // Мокирование PrintWriter для response
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream, true);
        when(response.getWriter()).thenReturn(printWriter);

        // Вызов сервлета для тестирования
        deleteUserServlet.doDelete(request, response);

//        verify(userService).deleteUser(eq(userDto.getId()), eq(adminUser));
//        assertFalse(byteArrayOutputStream.toString().isEmpty());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testGetAllUsersServletAsAdmin() throws Exception {
        when(request.getSession()).thenReturn(session);
        lenient().when(session.getAttribute("user")).thenReturn(adminUser);

        getAllUsersServlet.doGet(request, response);

        verify(userService).getAllUsers(adminUser);
        assertFalse(byteArrayOutputStream.toString().isEmpty());
    }

    @Test
    public void testGetUserServlet() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);

        byte[] input = objectMapper.writeValueAsBytes(userDto);
        when(request.getInputStream()).thenReturn(new ServletInputStreamAdapter(input));

        getUserServlet.doGet(request, response);

        verify(userService).getUser(userDto.getEmail(), adminUser);
        assertFalse(byteArrayOutputStream.toString().isEmpty());
    }

    @Test
    public void testUpdateUserProfileServlet() throws Exception {
        User currentUser = new User(UUID.randomUUID(), "test@example.com", "password", "Test User", false);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(currentUser);

        UserDto updatedUser = new UserDto(currentUser.getId(), "test@example.com", "newPassword", "New Name", false);
        byte[] input = objectMapper.writeValueAsBytes(updatedUser);
        when(request.getInputStream()).thenReturn(new ServletInputStreamAdapter(input));

        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream, true);
        when(response.getWriter()).thenReturn(printWriter);

        updateUserProfileServlet.doPost(request, response);

//        verify(userService).updateCurrentUserProfile(updatedUser.getName(), updatedUser.getPassword(), currentUser);
//        assertFalse(byteArrayOutputStream.toString().isEmpty());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
