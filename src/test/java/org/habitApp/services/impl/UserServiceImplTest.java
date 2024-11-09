package org.habitApp.services.impl;

import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.*;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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

    @Mock
    private AuthInMemoryContext authInMemoryContext;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDtoRegisterUpdate userDtoRegisterUpdate;
    private UserDtoLogin userDtoLogin;
    private UserEntity userEntity;
    private UserDto userDto;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        userDtoRegisterUpdate = new UserDtoRegisterUpdate("test@example.com", "Test User", "password123");
        userDtoLogin = new UserDtoLogin("test@example.com", "password123");
        userEntity = new UserEntity("test@example.com", "password123", "Test User", false);
        userDto = new UserDto("test@example.com", "password123", "Test User", false);
        testUser = new UserEntity(1, "test@example.com", "password123", "Test User", false);
    }

    @Test
    @DisplayName("Test: Update current user profile - User not found")
    void updateCurrentUserProfile_UserNotFound() throws SQLException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(authInMemoryContext);
            assertThrows(UserNotFoundException.class, () -> userService.updateCurrentUserProfile(userDtoRegisterUpdate));
        }
    }

    @Test
    @DisplayName("Test: Update current user profile - Successful update")
    void updateCurrentUserProfile_SuccessfulUpdate() throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(mock(AuthInMemoryContext.class));
            when(AuthInMemoryContext.getContext().getAuthentication()).thenReturn(testUser);

            when(userRepository.findByEmail(userDtoRegisterUpdate.getEmail())).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> userService.updateCurrentUserProfile(userDtoRegisterUpdate));
            verify(userRepository, times(1)).update(testUser);
        }
    }

    @Test
    @DisplayName("Test: Delete current user - Successful deletion")
    void deleteCurrentUser_SuccessfulDeletion() throws SQLException, UserNotFoundException {
        try (MockedStatic<AuthInMemoryContext> authInMemoryContextMock = mockStatic(AuthInMemoryContext.class)) {
            authInMemoryContextMock.when(AuthInMemoryContext::getContext).thenReturn(mock(AuthInMemoryContext.class));
            when(AuthInMemoryContext.getContext().getAuthentication()).thenReturn(testUser);

            assertDoesNotThrow(() -> userService.deleteCurrentUser());
            verify(userRepository, times(1)).deleteById(testUser.getId());
        }
    }

    @Test
    @DisplayName("Test: Delete current user - Unauthorized access")
    void deleteCurrentUser_UserNotFound() {
        assertThrows(UnauthorizedAccessException.class, () -> userService.deleteCurrentUser());
    }
}
