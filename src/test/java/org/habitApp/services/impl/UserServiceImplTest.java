package org.habitApp.services.impl;

import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.models.Role;
import org.habitApp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDtoRegisterUpdate userDtoRegisterUpdate;
    private UserEntity currentUser;
    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        userDtoRegisterUpdate = new UserDtoRegisterUpdate("new@example.com", "newpassword123", "Test User");
        currentUser = new UserEntity(1L, "current@example.com", "currentpassword", "Current User", Role.ROLE_USER);
        existingUser = new UserEntity(2L, "new@example.com", "password123", "Existing User", Role.ROLE_USER);
    }

    @Test
    @DisplayName("[updateCurrentUserProfile_UserNotFound] Обновление профиля текущего пользователя - Пользователь не найден")
    void updateCurrentUserProfile_UserNotFound() {
        assertThrows(UserNotFoundException.class, () ->
                userService.updateCurrentUserProfile(userDtoRegisterUpdate, null)
        );
    }

    @Test
    @DisplayName("[updateCurrentUserProfile_UserAlreadyExists] Обновление профиля текущего пользователя - Email уже используется")
    void updateCurrentUserProfile_UserAlreadyExists() throws SQLException {
        when(userRepository.findByEmail(userDtoRegisterUpdate.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.updateCurrentUserProfile(userDtoRegisterUpdate, currentUser)
        );
        verify(userRepository, never()).update(any());
    }

    @Test
    @DisplayName("[updateCurrentUserProfile_SuccessfulUpdate] Обновление профиля текущего пользователя - Успешное обновление")
    void updateCurrentUserProfile_SuccessfulUpdate() throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        when(userRepository.findByEmail(userDtoRegisterUpdate.getEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                userService.updateCurrentUserProfile(userDtoRegisterUpdate, currentUser)
        );
        verify(userRepository, times(1)).update(currentUser);
        assertEquals("new@example.com", currentUser.getEmail());
        assertEquals("Test User", currentUser.getUsername());
    }

    @Test
    @DisplayName("[deleteCurrentUser_SuccessfulDeletion] Удаление текущего пользователя - Успешное удаление")
    void deleteCurrentUser_SuccessfulDeletion() throws SQLException, UserNotFoundException {
        assertDoesNotThrow(() ->
                userService.deleteCurrentUser(currentUser)
        );
        verify(userRepository, times(1)).deleteById(currentUser.getId());
    }
}
