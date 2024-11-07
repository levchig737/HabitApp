package org.habitApp.services;

import org.habitApp.auth.JwtUtil;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Сервис для аутентификации пользователей.
 * Содержит бизнес-логику для регистрации, аутентификации пользователей.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, UserMapper userMapper, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param userDtoRegisterUpdate Данные для регистрации пользователя
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public void registerUser(UserDtoRegisterUpdate userDtoRegisterUpdate) throws UserAlreadyExistsException, SQLException {
        if (userRepository.getUserByEmail(userDtoRegisterUpdate.getEmail()) != null) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        UserEntity user = userMapper.userDtoRegisterUpdateToUser(userDtoRegisterUpdate);

        userRepository.registerUser(user);
    }

    /**
     * Вход пользователя в систему.
     *
     * @param userDtoLogin Данные для входа (email и пароль)
     * @return Данные пользователя после успешного входа
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws InvalidCredentialsException Если указаны неверные учетные данные
     */
    public String loginUser(UserDtoLogin userDtoLogin) throws SQLException, InvalidCredentialsException {
        UserEntity user = userRepository.getUserByEmail(userDtoLogin.getEmail());
        if (user != null && user.getPassword().equals(userDtoLogin.getPassword())) {
            return jwtUtil.generate(user.getId());
        }
        throw new InvalidCredentialsException("Invalid email or password.");
    }
}
