package org.habitApp.services;

import org.habitApp.models.User;
import org.habitApp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        adminUser = new User(UUID.randomUUID(), "admin@example.com", "password", "Admin User", true);
        regularUser = new User(UUID.randomUUID(), "user@example.com", "password", "Regular User", false);
    }

    @Test
    void testRegisterUser_Successful() throws SQLException {
        String email = "newuser@example.com";
        String password = "newpassword";
        String name = "New User";

        when(userRepository.getUserByEmail(email)).thenReturn(null);

        userService.registerUser(email, password, name);

        verify(userRepository, times(1)).registerUser(any(User.class));
    }

    @Test
    void testRegisterUser_AlreadyExists() throws SQLException {
        String email = "existing@example.com";
        String password = "password";
        String name = "Existing User";

        when(userRepository.getUserByEmail(email)).thenReturn(new User(UUID.randomUUID(), email, password, name, false));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(email, password, name));
    }

    @Test
    void testLoginUser_Successful() throws SQLException {
        String email = regularUser.getEmail();
        String password = regularUser.getPassword();

        when(userRepository.getUserByEmail(email)).thenReturn(regularUser);

        User user = userService.loginUser(email, password);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    void testLoginUser_InvalidCredentials() throws SQLException {
        String email = "nonexistent@example.com";
        String password = "wrongpassword";

        when(userRepository.getUserByEmail(email)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.loginUser(email, password));
    }

    @Test
    void testUpdateCurrentUserProfile_Successful() throws SQLException {
        String newName = "Updated User";
        String newPassword = "updatedPassword";

        userService.updateCurrentUserProfile(newName, newPassword, regularUser);

        assertEquals(newName, regularUser.getName());
        assertEquals(newPassword, regularUser.getPassword());
        verify(userRepository, times(1)).updateUser(regularUser);
    }

    @Test
    void testUpdateCurrentUserProfile_UserNotFound() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateCurrentUserProfile("New Name", "New Password", null));
    }

    @Test
    void testDeleteCurrentUser_Successful() throws SQLException {
        userService.deleteCurrentUser(regularUser);

        verify(userRepository, times(1)).deleteUserById(regularUser.getId());
    }

    @Test
    void testDeleteCurrentUser_UserNotFound() {
        assertThrows(IllegalArgumentException.class, () -> userService.deleteCurrentUser(null));
    }

    @Test
    void testGetUser_AdminAccess() throws SQLException, IllegalAccessException {
        when(userRepository.getUserByEmail(regularUser.getEmail())).thenReturn(regularUser);

        User user = userService.getUser(regularUser.getEmail(), adminUser);

        assertNotNull(user);
        assertEquals(regularUser.getEmail(), user.getEmail());
    }

    @Test
    void testGetUser_NonAdminAccess() {
        assertThrows(IllegalAccessException.class, () -> userService.getUser(regularUser.getEmail(), regularUser));
    }

    @Test
    void testGetAllUsers_AdminAccess() throws SQLException, IllegalAccessException {
        when(userRepository.getAllUsers()).thenReturn(List.of(adminUser, regularUser));

        List<User> users = userService.getAllUsers(adminUser);

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testGetAllUsers_NonAdminAccess() {
        assertThrows(IllegalAccessException.class, () -> userService.getAllUsers(regularUser));
    }

    @Test
    void testUpdateUserProfile_AdminAccess() throws SQLException, IllegalAccessException {
        String email = regularUser.getEmail();
        String newName = "Updated Name";
        String newPassword = "UpdatedPassword";
        boolean isAdmin = true;

        when(userRepository.getUserByEmail(email)).thenReturn(regularUser);

        User updatedUser = userService.updateUserProfile(email, newName, newPassword, isAdmin, adminUser);

        assertEquals(newName, updatedUser.getName());
        assertEquals(newPassword, updatedUser.getPassword());
        assertTrue(updatedUser.isAdmin());
        verify(userRepository, times(1)).updateUser(updatedUser);
    }

    @Test
    void testUpdateUserProfile_NonAdminAccess() {
        assertThrows(IllegalAccessException.class, () -> userService.updateUserProfile(regularUser.getEmail(), "Name", "Password", true, regularUser));
    }

    @Test
    void testDeleteUser_AdminAccess() throws SQLException, IllegalAccessException {
        UUID userId = regularUser.getId();

        userService.deleteUser(userId, adminUser);

        verify(userRepository, times(1)).deleteUserById(userId);
    }

    @Test
    void testDeleteUser_NonAdminAccess() {
        assertThrows(IllegalAccessException.class, () -> userService.deleteUser(regularUser.getId(), regularUser));
    }
}
