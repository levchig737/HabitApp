package org.habitApp.services.impl;

import lombok.RequiredArgsConstructor;
import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.repositories.UserRepository;
import org.habitApp.services.UserService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 * Содержит бизнес-логику для обновления и удаления пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository; // Репозиторий для работы с данными пользователей
    private final UserMapper userMapper; // Маппер для преобразования между UserDto и UserEntity

    /**
     * Обновление профиля текущего пользователя.
     *
     * @param userDtoRegisterUpdate Данные для обновления профиля
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует
     */
    @Override
    public void updateCurrentUserProfile(UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        if (currentUser != null) {
            if (userRepository.findByEmail(userDtoRegisterUpdate.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("User already exists.");
            }

            currentUser.setEmail(userDtoRegisterUpdate.getEmail());
            currentUser.setName(userDtoRegisterUpdate.getName());
            currentUser.setPassword(userDtoRegisterUpdate.getPassword());
            userRepository.update(currentUser);
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
    @Override
    public void deleteCurrentUser() throws SQLException, UserNotFoundException {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();
        userRepository.deleteById(currentUser.getId());
    }

    /**
     * Получение информации о пользователе по email (доступно только администраторам).
     *
     * @param email Email пользователя
     * @return Данные запрашиваемого пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    @Override
    public UserDto getUser(String email) throws SQLException {

        Optional<UserEntity> user = userRepository.findByEmail(email);
        return userMapper.userToUserDto(user.orElse(null));
    }

    /**
     * Получение списка всех пользователей (доступно только администраторам).
     *
     * @return Список пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    @Override
    public List<UserDto> getAllUsers() throws SQLException, UnauthorizedAccessException {

        List<UserEntity> users = userRepository.findAll();
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
    @Override
    public UserDto updateUserProfile(long id, UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UnauthorizedAccessException, UserNotFoundException {

        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setName(userDtoRegisterUpdate.getName());
            user.get().setPassword(userDtoRegisterUpdate.getPassword());
            userRepository.update(user.get());
            return userMapper.userToUserDto(user.orElse(null));
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
    @Override
    public void deleteUser(long id) throws SQLException, UnauthorizedAccessException {
        userRepository.deleteById(id);
    }

    /**
     * Метод для получения пользователя для аутентификации
     * @param id id
     * @return UserEntity
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    @Override
    public UserEntity findByEmailForAuthentication(Long id) throws SQLException {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.orElse(null);
    }
}
