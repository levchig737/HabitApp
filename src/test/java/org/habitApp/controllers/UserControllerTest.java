package org.habitApp.controllers;

import org.habitApp.domain.dto.userDto.UserDto;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.exceptions.UserNotFoundException;
import org.habitApp.mappers.UserMapper;
import org.habitApp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("PUT /users/profile - Успешное обновление профиля текущего пользователя")
    void updateCurrentUserProfile_Success() throws Exception {
        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /users/profile - Обновление профиля текущего пользователя, пользователь не найден")
    void updateCurrentUserProfile_UserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).updateCurrentUserProfile(any(), any());

        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("DELETE /users/profile - Удаление профиля текущего пользователя")
    void deleteCurrentUser_Success() throws Exception {
        mockMvc.perform(delete("/users/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/profile - Удаление профиля текущего пользователя, пользователь не найден")
    void deleteCurrentUser_UserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteCurrentUser(any());

        mockMvc.perform(delete("/users/profile"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("GET /users/admin/{email} - Получение пользователя по email")
    void getUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(userService.getUser(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/admin/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /users/admin/{email} - Получение пользователя по email, не авторизован")
    void getUser_Unauthorized() throws Exception {
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(userService).getUser(any());

        mockMvc.perform(get("/users/admin/test@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    @DisplayName("GET /users/admin - Получение всех пользователей")
    void getAllUsers_Success() throws Exception {
        List<UserDto> users = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users/admin"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /users/admin - Получение всех пользователей, не авторизован")
    void getAllUsers_Unauthorized() throws Exception {
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(userService).getAllUsers();

        mockMvc.perform(get("/users/admin"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    @DisplayName("PUT /users/admin/{id} - Обновление профиля пользователя по ID")
    void updateUserProfile_Success() throws Exception {
        UserDtoRegisterUpdate updateDto = new UserDtoRegisterUpdate();
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setEmail("test@example.com");
        when(userService.updateUserProfile(anyLong(), any())).thenReturn(updatedUserDto);

        mockMvc.perform(put("/users/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("PUT /users/admin/{id} - Обновление профиля пользователя по ID, пользователь не найден")
    void updateUserProfile_UserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found"))
                .when(userService).updateUserProfile(anyLong(), any());

        mockMvc.perform(put("/users/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("DELETE /users/admin/{id} - Удаление пользователя по ID")
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/users/admin/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/admin/{id} - Удаление пользователя по ID, не авторизован")
    void deleteUser_Unauthorized() throws Exception {
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/admin/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unauthorized"));
    }
}
