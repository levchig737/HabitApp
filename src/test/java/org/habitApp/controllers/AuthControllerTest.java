package org.habitApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.models.Role;
import org.habitApp.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDtoRegisterUpdate userDtoRegisterUpdate;
    private UserDtoLogin userDtoLogin;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        userDtoRegisterUpdate = new UserDtoRegisterUpdate("test@example.com", "password", "Test User");
        userDtoLogin = new UserDtoLogin("test@example.com", "password");
        testUser = new UserEntity(1L, "test@example.com", "password", "Test User", Role.ROLE_USER);
    }

    @Test
    @DisplayName("POST /auth/register - Регистрация нового пользователя")
    void shouldRegisterUser() throws Exception {
        doNothing().when(authService).registerUser(userDtoRegisterUpdate);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoRegisterUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/register - Ошибка регистрации при существующем пользователе")
    void shouldReturnErrorWhenUserAlreadyExists() throws Exception {
        doThrow(new UserAlreadyExistsException("User already exists")).when(authService).registerUser(userDtoRegisterUpdate);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoRegisterUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists"));
    }

    @Test
    @DisplayName("POST /auth/login - Успешный вход пользователя")
    void shouldLoginUser() throws Exception {
        String token = "jwt_token_example";
        when(authService.loginUser(userDtoLogin)).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoLogin)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }


    @Test
    @DisplayName("POST /auth/login - Ошибка входа при неверных учетных данных")
    void shouldReturnErrorWhenInvalidCredentials() throws Exception {
        doThrow(new InvalidCredentialsException("Invalid credentials")).when(authService).loginUser(userDtoLogin);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("GET /auth/test - Проверка авторизации пользователя")
    void shouldReturnAuthenticatedUser() throws Exception {
        AuthInMemoryContext.getContext().setAuthentication(testUser);

        mockMvc.perform(get("/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string(testUser.toString()));
    }

    @Test
    @DisplayName("GET /auth/admin/test - Проверка авторизации администратора")
    void shouldReturnAdminAccessForAuthorizedUser() throws Exception {
        UserEntity adminUser = new UserEntity(1L, "admin@example.com", "password", "Admin User", Role.ROLE_ADMIN);
        AuthInMemoryContext.getContext().setAuthentication(adminUser);

        mockMvc.perform(get("/auth/admin/test"))
                .andExpect(status().isOk())
                .andExpect(content().string(adminUser.toString()));
    }
}
