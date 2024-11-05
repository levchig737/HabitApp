package org.habitApp.controllers;

import org.habitApp.config.beans.CurrentUserBean;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.exceptions.*;
import org.habitApp.mappers.UserMapper;
import org.habitApp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CurrentUserBean currentUserBean;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // registerUser
    @Test
    void registerUser_SuccessfulRegistration() throws SQLException, UserAlreadyExistsException {
        UserDtoRegisterUpdate newUser = new UserDtoRegisterUpdate();
        ResponseEntity<?> response = userController.registerUser(newUser);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).registerUser(newUser);
    }

    @Test
    void registerUser_UserAlreadyExists() throws SQLException, UserAlreadyExistsException {
        UserDtoRegisterUpdate newUser = new UserDtoRegisterUpdate();
        doThrow(UserAlreadyExistsException.class).when(userService).registerUser(newUser);

        ResponseEntity<?> response = userController.registerUser(newUser);
        assertEquals(400, response.getStatusCodeValue());
    }

    // loginUser
    @Test
    void loginUser_SuccessfulLogin() throws SQLException, InvalidCredentialsException {
        UserDtoLogin loginData = new UserDtoLogin();
        UserDto userDto = new UserDto();
        when(userService.loginUser(loginData)).thenReturn(userDto);
        when(userMapper.userDtoToUser(userDto)).thenReturn(any());

        ResponseEntity<?> response = userController.loginUser(loginData);
        assertEquals(200, response.getStatusCodeValue());
        verify(currentUserBean).setCurrentUser(any());
    }

    @Test
    void loginUser_InvalidCredentials() throws SQLException, InvalidCredentialsException {
        UserDtoLogin loginData = new UserDtoLogin();
        when(userService.loginUser(loginData)).thenThrow(InvalidCredentialsException.class);

        ResponseEntity<?> response = userController.loginUser(loginData);
        assertEquals(400, response.getStatusCodeValue());
    }

    // logoutUser
    @Test
    void logoutUser_SuccessfulLogout() {
        when(currentUserBean.isAuthenticated()).thenReturn(true);

        ResponseEntity<?> response = userController.logoutUser();
        assertEquals(200, response.getStatusCodeValue());
        verify(currentUserBean).setCurrentUser(null);
    }

    @Test
    void logoutUser_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = userController.logoutUser();
        assertEquals(401, response.getStatusCodeValue());
    }

    // updateCurrentUserProfile
    @Test
    void updateCurrentUserProfile_SuccessfulUpdate() throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        UserDtoRegisterUpdate updateData = new UserDtoRegisterUpdate();
        when(currentUserBean.isAuthenticated()).thenReturn(true);

        ResponseEntity<?> response = userController.updateCurrentUserProfile(updateData);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).updateCurrentUserProfile(updateData, currentUserBean.getCurrentUser());
    }

    // deleteCurrentUser
    @Test
    void deleteCurrentUser_SuccessfulDeletion() throws SQLException, UserNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);

        ResponseEntity<?> response = userController.deleteCurrentUser();
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).deleteCurrentUser(currentUserBean.getCurrentUser());
    }

    @Test
    void deleteCurrentUser_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = userController.deleteCurrentUser();
        assertEquals(401, response.getStatusCodeValue());
    }

    // getUser
    @Test
    void getUser_AdminAccess() throws SQLException, UnauthorizedAccessException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        UserDto userDto = new UserDto();
        when(userService.getUser("test@example.com", currentUserBean.getCurrentUser())).thenReturn(userDto);

        ResponseEntity<?> response = userController.getUser("test@example.com");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getUser_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = userController.getUser("test@example.com");
        assertEquals(401, response.getStatusCodeValue());
    }

    // getAllUsers
    @Test
    void getAllUsers_AdminAccess() throws SQLException, UnauthorizedAccessException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        when(userService.getAllUsers(currentUserBean.getCurrentUser())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = userController.getAllUsers();
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getAllUsers_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = userController.getAllUsers();
        assertEquals(401, response.getStatusCodeValue());
    }

    // updateUserProfile
    @Test
    void updateUserProfile_AdminAccess() throws SQLException, UnauthorizedAccessException, UserNotFoundException {
        UserDtoRegisterUpdate updateData = new UserDtoRegisterUpdate();
        when(currentUserBean.isAuthenticated()).thenReturn(true);

        ResponseEntity<?> response = userController.updateUserProfile(1, updateData);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateUserProfile_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = userController.updateUserProfile(1, new UserDtoRegisterUpdate());
        assertEquals(401, response.getStatusCodeValue());
    }

    // deleteUser
    @Test
    void deleteUser_AdminAccess() throws SQLException, UnauthorizedAccessException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);

        ResponseEntity<?> response = userController.deleteUser(1);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).deleteUser(1, currentUserBean.getCurrentUser());
    }

    @Test
    void deleteUser_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = userController.deleteUser(1);
        assertEquals(401, response.getStatusCodeValue());
    }
}
