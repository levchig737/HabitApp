package org.habitApp.controllers;

import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.services.AuthService;
import org.habitApp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("[Register User - Successful registration]")
    void testRegisterUser_Success() throws SQLException, UserAlreadyExistsException {
        UserDtoRegisterUpdate userDto = new UserDtoRegisterUpdate();
        doNothing().when(authService).registerUser(userDto);

        ResponseEntity<?> response = authController.registerUser(userDto);

        assertEquals(200, response.getStatusCodeValue());
        verify(authService, times(1)).registerUser(userDto);
    }

    @Test
    @DisplayName("[Register User - User already exists]")
    void testRegisterUser_UserAlreadyExists() throws SQLException, UserAlreadyExistsException {
        UserDtoRegisterUpdate userDto = new UserDtoRegisterUpdate();
        doThrow(new UserAlreadyExistsException("User already exists")).when(authService).registerUser(userDto);

        ResponseEntity<?> response = authController.registerUser(userDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody());
        verify(authService, times(1)).registerUser(userDto);
    }

    @Test
    @DisplayName("[Login - Successful login]")
    void testLogin_Success() throws SQLException, InvalidCredentialsException {
        UserDtoLogin userDtoLogin = new UserDtoLogin();

        when(authService.loginUser(userDtoLogin)).thenReturn("token");

        ResponseEntity<?> response = authController.login(userDtoLogin, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token", response.getBody());
        verify(authService, times(1)).loginUser(userDtoLogin);
    }

    @Test
    @DisplayName("[Login - Invalid credentials]")
    void testLogin_InvalidCredentials() throws SQLException, InvalidCredentialsException {
        UserDtoLogin userDtoLogin = new UserDtoLogin();
        when(authService.loginUser(userDtoLogin)).thenThrow(new InvalidCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(userDtoLogin, null);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
        verify(authService, times(1)).loginUser(userDtoLogin);
    }

    @Test
    @WithMockUser
    @DisplayName("[Test Auth - Successful authentication]")
    void testTestAuth_Success() {
        UserEntity mockUser = new UserEntity();
        mockUser.setEmail("test@example.com");

        ResponseEntity<?> response = authController.testAuth(mockUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("[Test Admin - Successful admin authentication]")
    void testTestAdmin_Success() {
        UserEntity mockAdmin = new UserEntity();
        mockAdmin.setEmail("admin@example.com");

        ResponseEntity<?> response = authController.testAdmin(mockAdmin);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAdmin, response.getBody());
    }
}