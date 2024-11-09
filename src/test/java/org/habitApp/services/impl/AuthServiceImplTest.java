package org.habitApp.services.impl;

import org.habitApp.auth.JwtUtil;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserDtoRegisterUpdate userDtoRegisterUpdate;
    private UserDtoLogin userDtoLogin;

    @BeforeEach
    void setUp() {
        userDtoRegisterUpdate = new UserDtoRegisterUpdate("test@example.com", "Test User", "password");
        userDtoLogin = new UserDtoLogin("test@example.com", "password");
    }

    @Test
    @DisplayName("Регистрирует пользователя успешно")
    void registerUser_success() throws SQLException, UserAlreadyExistsException {
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByEmail(userDtoRegisterUpdate.getEmail())).thenReturn(Optional.empty());
        when(userMapper.userDtoRegisterUpdateToUser(userDtoRegisterUpdate)).thenReturn(userEntity);

        authService.registerUser(userDtoRegisterUpdate);

        verify(userRepository, times(1)).create(userEntity);
    }

    @Test
    @DisplayName("Ошибка регистрации: пользователь с таким email уже существует")
    void registerUser_userAlreadyExists() throws SQLException {
        when(userRepository.findByEmail(userDtoRegisterUpdate.getEmail())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(userDtoRegisterUpdate));
    }

    @Test
    @DisplayName("Успешный вход пользователя")
    void loginUser_success() throws SQLException, InvalidCredentialsException {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("password");
        userEntity.setId(1L);
        when(userRepository.findByEmail(userDtoLogin.getEmail())).thenReturn(Optional.of(userEntity));
        when(jwtUtil.generate(userEntity.getId())).thenReturn("mocked_jwt_token");

        String token = authService.loginUser(userDtoLogin);

        assertEquals("mocked_jwt_token", token);
    }

    @Test
    @DisplayName("Ошибка входа: неверный пароль")
    void loginUser_invalidCredentials() throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("correct_password");
        when(userRepository.findByEmail(userDtoLogin.getEmail())).thenReturn(Optional.of(userEntity));

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(userDtoLogin));
    }

    @Test
    @DisplayName("Ошибка входа: пользователь не найден")
    void loginUser_userNotFound() throws SQLException {
        when(userRepository.findByEmail(userDtoLogin.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(userDtoLogin));
    }
}
