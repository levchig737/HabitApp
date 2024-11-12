package org.habitApp.services;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для работы с пользователями.
 * Содержит методы для обновления, удаления пользователей и получения информации.
 */
public interface UserService {

    /**
     * Обновление профиля текущего пользователя.
     *
     * @param userDtoRegisterUpdate Данные для обновления профиля
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует
     */
    void updateCurrentUserProfile(UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UserNotFoundException, UserAlreadyExistsException;

    /**
     * Удаление текущего пользователя.
     *
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     */
    void deleteCurrentUser() throws SQLException, UserNotFoundException;

    /**
     * Получение информации о пользователе по email (доступно только администраторам).
     *
     * @param email Email пользователя
     * @return Данные запрашиваемого пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    UserDto getUser(String email) throws SQLException, UnauthorizedAccessException;

    /**
     * Получение списка всех пользователей (доступно только администраторам).
     *
     * @return Список пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    List<UserDto> getAllUsers() throws SQLException, UnauthorizedAccessException;

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
    UserDto updateUserProfile(long id, UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UnauthorizedAccessException, UserNotFoundException;

    /**
     * Удаление пользователя по ID (доступно только администраторам).
     *
     * @param id ID пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UnauthorizedAccessException Если текущий пользователь не является администратором
     */
    void deleteUser(long id) throws SQLException, UnauthorizedAccessException;

    /**
     * Метод для получения пользователя для аутентификации
     * @param id id
     * @return UserEntity
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    UserEntity findByEmailForAuthentication(Long id) throws SQLException;
}
