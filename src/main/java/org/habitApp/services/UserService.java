package org.habitApp.services;

import org.habitApp.annotations.Loggable;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.InvalidCredentialsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с пользователями.
 * Содержит бизнес-логику для регистрации, аутентификации, обновления и удаления пользователей.
 */
@Loggable
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Репозиторий для работы с данными пользователей

    @Autowired
    private UserMapper userMapper; // Маппер для преобразования между UserDto и UserEntity

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

        // Генерируем UUID для нового пользователя
        user.setId(UUID.randomUUID());

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
    public UserDto loginUser(UserDtoLogin userDtoLogin) throws SQLException, InvalidCredentialsException {
        UserEntity user = userRepository.getUserByEmail(userDtoLogin.getEmail());
        if (user != null && user.getPassword().equals(userDtoLogin.getPassword())) {
            return userMapper.userToUserDto(user);
        }
        throw new InvalidCredentialsException("Invalid email or password.");
    }

    /**
     * Обновление профиля текущего пользователя.
     *
     * @param userDtoRegisterUpdate Данные для обновления профиля
     * @param currentUser Текущий пользователь
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует
     */
    public void updateCurrentUserProfile(UserDtoRegisterUpdate userDtoRegisterUpdate, UserEntity currentUser)
            throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        if (currentUser != null) {
            if (userRepository.getUserByEmail(userDtoRegisterUpdate.getEmail()) != null) {
                throw new UserAlreadyExistsException("User already exists.");
            }

            currentUser.setEmail(userDtoRegisterUpdate.getEmail());
            currentUser.setName(userDtoRegisterUpdate.getName());
            currentUser.setPassword(userDtoRegisterUpdate.getPassword());
            userRepository.updateUser(currentUser);
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    /**
     * Удаление текущего пользователя.
     *
     * @param currentUser Текущий пользователь
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     */
    public void deleteCurrentUser(UserEntity currentUser) throws SQLException, UserNotFoundException {
        if (currentUser != null) {
            userRepository.deleteUserById(currentUser.getId());
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    /**
     * Получение информации о пользователе по email (доступно только администраторам).
     *
     * @param email Email пользователя
     * @param currentUser Текущий пользователь
     * @return Данные запрашиваемого пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    public UserDto getUser(String email, UserEntity currentUser) throws SQLException, UnauthorizedAccessException {
        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        UserEntity user = userRepository.getUserByEmail(email);
        return userMapper.userToUserDto(user);
    }

    /**
     * Получение списка всех пользователей (доступно только администраторам).
     *
     * @param currentUser Текущий пользователь
     * @return Список пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    public List<UserDto> getAllUsers(UserEntity currentUser) throws SQLException, UnauthorizedAccessException {
        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        List<UserEntity> users = userRepository.getAllUsers();
        return users.stream().map(userMapper::userToUserDto).toList();
    }

    /**
     * Обновление профиля пользователя по ID (доступно только администраторам).
     *
     * @param id ID пользователя
     * @param userDto Данные для обновления
     * @param currentUser Текущий пользователь
     * @return Данные обновленного пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     * @throws UserNotFoundException Если пользователь с указанным ID не найден
     */
    public UserDto updateUserProfile(UUID id, UserDto userDto, UserEntity currentUser)
            throws SQLException, UnauthorizedAccessException, UserNotFoundException {
        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        UserEntity user = userRepository.getUserById(id);
        if (user != null) {
            user.setName(userDto.getName());
            user.setPassword(userDto.getPassword());
            user.setFlagAdmin(userDto.isFlagAdmin());
            userRepository.updateUser(user);
            return userMapper.userToUserDto(user);
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    /**
     * Удаление пользователя по ID (доступно только администраторам).
     *
     * @param id ID пользователя
     * @param currentUser Текущий пользователь
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    public void deleteUser(UUID id, UserEntity currentUser) throws SQLException, UnauthorizedAccessException {
        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        userRepository.deleteUserById(id);
    }
}
