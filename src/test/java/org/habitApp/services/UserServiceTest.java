package org.habitApp.services;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.*;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.impl.UserRepositoryImpl;
import org.habitApp.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDtoRegisterUpdate userDtoRegister;
    private UserDtoLogin userDtoLogin;
    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDtoRegister = new UserDtoRegisterUpdate("test@example.com", "Test User", "password123");
        userDtoLogin = new UserDtoLogin("test@example.com", "password123");
        userEntity = new UserEntity("test@example.com", "password123", "Test User", false);
        userDto = new UserDto("test@example.com", "password", "Test User", false);
    }

    @Test
    void registerUser_SuccessfulRegistration() throws SQLException, UserAlreadyExistsException {
        when(userRepository.getUserByEmail(userDtoRegister.getEmail())).thenReturn(null);
        when(userMapper.userDtoRegisterUpdateToUser(userDtoRegister)).thenReturn(userEntity);

        assertDoesNotThrow(() -> userService.registerUser(userDtoRegister));

        verify(userRepository, times(1)).registerUser(userEntity);
    }

    @Test
    void registerUser_UserAlreadyExists() throws SQLException {
        when(userRepository.getUserByEmail(userDtoRegister.getEmail())).thenReturn(userEntity);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userDtoRegister));
    }

    @Test
    void loginUser_SuccessfulLogin() throws SQLException, InvalidCredentialsException {
        when(userRepository.getUserByEmail(userDtoLogin.getEmail())).thenReturn(userEntity);
        when(userMapper.userToUserDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.loginUser(userDtoLogin);

        assertEquals(userDto, result);
        verify(userRepository, times(1)).getUserByEmail(userDtoLogin.getEmail());
    }

    @Test
    void loginUser_InvalidCredentials() throws SQLException {
        when(userRepository.getUserByEmail(userDtoLogin.getEmail())).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(userDtoLogin));
    }

    @Test
    void updateCurrentUserProfile_UserNotFound() throws SQLException {
        assertThrows(UserNotFoundException.class, () -> userService.updateCurrentUserProfile(userDtoRegister, null));
    }

    @Test
    void updateCurrentUserProfile_SuccessfulUpdate() throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        UserEntity currentUser = new UserEntity("old@example.com", "Old User", "oldPassword", false);

        when(userRepository.getUserByEmail(userDtoRegister.getEmail())).thenReturn(null);

        assertDoesNotThrow(() -> userService.updateCurrentUserProfile(userDtoRegister, currentUser));
        verify(userRepository, times(1)).updateUser(currentUser);
    }

    @Test
    void deleteCurrentUser_SuccessfulDeletion() throws SQLException, UserNotFoundException {
        UserEntity currentUser = new UserEntity("test@example.com", "Test User", "password123", false);

        assertDoesNotThrow(() -> userService.deleteCurrentUser(currentUser));
        verify(userRepository, times(1)).deleteUserById(currentUser.getId());
    }

    @Test
    void deleteCurrentUser_UserNotFound() throws SQLException {
        assertThrows(UserNotFoundException.class, () -> userService.deleteCurrentUser(null));
    }

    @Test
    void getUser_UnauthorizedAccess() {
        UserEntity nonAdminUser = new UserEntity("nonadmin@example.com", "Non-Admin", "password123", false);

        assertThrows(UnauthorizedAccessException.class, () -> userService.getUser("target@example.com", nonAdminUser));
    }

    @Test
    void getAllUsers_UnauthorizedAccess() {
        UserEntity nonAdminUser = new UserEntity("nonadmin@example.com", "Non-Admin", "password123", false);

        assertThrows(UnauthorizedAccessException.class, () -> userService.getAllUsers(nonAdminUser));
    }

    @Test
    void updateUserProfile_UnauthorizedAccess() {
        UserEntity nonAdminUser = new UserEntity("nonadmin@example.com", "Non-Admin", "password123", false);

        assertThrows(UnauthorizedAccessException.class, () -> userService.updateUserProfile(1L, userDtoRegister, nonAdminUser));
    }

    @Test
    void deleteUser_UnauthorizedAccess() {
        UserEntity nonAdminUser = new UserEntity("nonadmin@example.com", "Non-Admin", "password123", false);

        assertThrows(UnauthorizedAccessException.class, () -> userService.deleteUser(1L, nonAdminUser));
    }
}
