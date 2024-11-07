package org.habitApp.services;

import org.habitApp.auth.AuthInMemoryContext;
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
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервис для работы с пользователями.
 * Содержит бизнес-логику для обновления и удаления пользователей.
 */
@Service
public class UserService {
    private final UserRepository userRepository; // Репозиторий для работы с данными пользователей
    private final UserMapper userMapper; // Маппер для преобразования между UserDto и UserEntity

    /**
     * Констрктор UserService
     * @param userRepository userRepository
     * @param userMapper userMapper
     */
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Обновление профиля текущего пользователя.
     *
     * @param userDtoRegisterUpdate Данные для обновления профиля
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует
     */
    public void updateCurrentUserProfile(UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
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
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     */
    public void deleteCurrentUser() throws SQLException, UserNotFoundException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser != null) {
            userRepository.deleteUserById(currentUser.getId());
        } else {
            throw new UnauthorizedAccessException("Unauthorized");
        }
    }

    /**
     * Получение информации о пользователе по email (доступно только администраторам).
     *
     * @param email Email пользователя
     * @return Данные запрашиваемого пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    public UserDto getUser(String email) throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();

        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        UserEntity user = userRepository.getUserByEmail(email);
        return userMapper.userToUserDto(user);
    }

    /**
     * Получение списка всех пользователей (доступно только администраторам).
     *
     * @return Список пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    public List<UserDto> getAllUsers() throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();

        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

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
     * @param userDtoRegisterUpdate Данные для обновления
     * @return Данные обновленного пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     * @throws UserNotFoundException Если пользователь с указанным ID не найден
     */
    public UserDto updateUserProfile(long id, UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UnauthorizedAccessException, UserNotFoundException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();

        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        UserEntity user = userRepository.getUserById(id);
        if (user != null) {
            user.setName(userDtoRegisterUpdate.getName());
            user.setPassword(userDtoRegisterUpdate.getPassword());
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
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    public void deleteUser(long id) throws SQLException, UnauthorizedAccessException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();

        if (currentUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }

        if (!currentUser.isFlagAdmin()) {
            throw new UnauthorizedAccessException("User is not admin.");
        }
        userRepository.deleteUserById(id);
    }

    /**
     * Метод для получения пользователя для аутентификации
     * @param id id
     * @return UserEntity
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    public UserEntity findByEmailForAuthentication(Long id) throws SQLException {
        return userRepository.getUserById(id);
    }
}
