package org.habitApp.services;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UserAlreadyExistsException;
import org.habitApp.exceptions.UserNotFoundException;

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
     * @param currentUser текущий пользователь
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     * @throws UserAlreadyExistsException Если пользователь с таким email уже существует
     */
    void updateCurrentUserProfile(UserDtoRegisterUpdate userDtoRegisterUpdate, UserEntity currentUser)
            throws SQLException, UserNotFoundException, UserAlreadyExistsException;

    /**
     * Удаление текущего пользователя.
     *
     * @param currentUser текущий пользователь
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если текущий пользователь не найден
     */
    void deleteCurrentUser(UserEntity currentUser) throws SQLException, UserNotFoundException;

    /**
     * Получение информации о пользователе по email (доступно только администраторам).
     *
     * @param email Email пользователя
     * @return Данные запрашиваемого пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    UserDto getUser(String email) throws SQLException;

    /**
     * Получение списка всех пользователей (доступно только администраторам).
     *
     * @return Список пользователей
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    List<UserDto> getAllUsers() throws SQLException;

    /**
     * Обновление профиля пользователя по ID (доступно только администраторам).
     *
     * @param id ID пользователя
     * @param userDtoRegisterUpdate Данные для обновления
     * @return Данные обновленного пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     * @throws UserNotFoundException Если пользователь с указанным ID не найден
     */
    UserDto updateUserProfile(long id, UserDtoRegisterUpdate userDtoRegisterUpdate)
            throws SQLException, UserNotFoundException;

    /**
     * Удаление пользователя по ID (доступно только администраторам).
     *
     * @param id ID пользователя
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    void deleteUser(long id) throws SQLException;

    /**
     * Метод для получения пользователя для аутентификации
     * @param email email
     * @return UserEntity
     * @throws SQLException В случае ошибок при работе с базой данных
     */
    UserEntity findByEmailForAuthentication(String email) throws SQLException;
}
