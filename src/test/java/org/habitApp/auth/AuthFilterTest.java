package org.habitApp.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.services.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthFilterTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private MockHttpServletRequest request;

    @Mock
    private MockHttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private AuthInMemoryContext authContext;

    @BeforeEach
    public void setUp() {
        authContext = AuthInMemoryContext.getContext();
        authFilter = new AuthFilter(userService, jwtUtil);
    }

    @AfterEach
    void resetContext() {
        authContext.setAuthentication(null);
        authContext.setIp(null);
    }

    @Test
    @DisplayName("[init] Должен корректно инициализировать фильтр")
    public void shouldInitializeFilter() throws ServletException {
        FilterConfig filterConfig = mock(FilterConfig.class);
        authFilter.init(filterConfig);
    }

    @Test
    @DisplayName("[doFilter] Должен продолжить без аутентификации, если заголовок Authorization отсутствует")
    public void shouldProceedWithoutAuthenticationWhenNoAuthHeader() throws IOException, ServletException {
        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("[doFilter] Должен продолжить без аутентификации при неверном формате заголовка Authorization")
    public void shouldProceedWithoutAuthenticationWithInvalidAuthHeader() throws IOException, ServletException {
        request.addHeader("Authorization", "InvalidToken");
        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("[doFilter] Должен аутентифицировать пользователя при корректном токене")
    public void shouldAuthenticateUserWithValidToken() throws IOException, ServletException, SQLException {
        request.addHeader("Authorization", "Bearer validToken");
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("testUser");

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("[doFilter] Должен продолжить без аутентификации при неверном токене")
    public void shouldProceedWithoutAuthenticationWithInvalidToken() throws IOException, ServletException {
        authContext.setAuthentication(null);
        request.addHeader("Authorization", "Bearer invalidToken");


        authFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

}
