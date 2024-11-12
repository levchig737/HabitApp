package org.habitApp.auth;

import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthInMemoryContextTest {

    private AuthInMemoryContext authContext;

    private UserEntity mockUser;

    @BeforeEach
    public void setUp() {
        authContext = AuthInMemoryContext.getContext();
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setName("testUser");
    }

    @AfterEach
    void resetContext() {
        authContext.setAuthentication(null);
        authContext.setIp(null);
    }

    @Test
    @DisplayName("[getContext] Должен возвращать один и тот же экземпляр (Singleton)")
    public void shouldReturnSingletonInstance() {
        AuthInMemoryContext anotherContext = AuthInMemoryContext.getContext();
        assertThat(authContext).isSameAs(anotherContext);
    }

    @Test
    @DisplayName("[getAuthentication] Должен выбрасывать исключение, если пользователь не аутентифицирован")
    public void shouldThrowExceptionWhenUserNotAuthenticated() {
        assertThrows(UnauthorizedAccessException.class, authContext::getAuthentication);
    }

    @Test
    @DisplayName("[setAuthentication] Должен возвращать аутентифицированного пользователя после установки")
    public void shouldReturnAuthenticatedUserAfterSetAuthentication() {
        authContext.setAuthentication(mockUser);
        assertThat(authContext.getAuthentication()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("[setIp/getIp] Должен корректно сохранять и возвращать IP-адрес")
    public void shouldStoreAndRetrieveIpCorrectly() {
        String testIp = "192.168.1.1";
        authContext.setIp(testIp);
        assertThat(authContext.getIp()).isEqualTo(testIp);
    }

    @Test
    @DisplayName("[setAuthentication, setIp] Должен сбрасывать состояние между тестами")
    public void shouldClearAuthenticationStateBetweenTests() {
        authContext.setAuthentication(mockUser);
        authContext.setIp("192.168.1.1");

        authContext.setAuthentication(null);
        authContext.setIp(null);

        assertThrows(UnauthorizedAccessException.class, authContext::getAuthentication);
        assertThat(authContext.getIp()).isNull();
    }
}
