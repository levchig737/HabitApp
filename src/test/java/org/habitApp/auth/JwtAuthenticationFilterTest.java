package org.habitApp.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.habitApp.services.impl.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("[doFilterInternal] Должен продолжить без аутентификации, если заголовок Authorization отсутствует")
    void shouldProceedWithoutAuthenticationWhenNoAuthHeader() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    @DisplayName("[doFilterInternal] Должен продолжить без аутентификации при неверном формате заголовка Authorization")
    void shouldProceedWithoutAuthenticationWithInvalidAuthHeader() throws ServletException, IOException {
        request.addHeader("Authorization", "InvalidTokenFormat");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    @DisplayName("[doFilterInternal] Должен аутентифицировать пользователя при корректном токене")
    void shouldAuthenticateUserWithValidToken() throws ServletException, IOException {
        String validToken = "validToken";
        String email = "test@example.com";
        UserDetails userDetails = mock(UserDetails.class);

        request.addHeader("Authorization", "Bearer " + validToken);
        when(jwtUtil.verifyAndGetUserId(validToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, times(1)).verifyAndGetUserId(validToken);
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verify(filterChain, times(1)).doFilter(request, response);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        assert authentication.getPrincipal() == userDetails;
    }

    @Test
    @DisplayName("[doFilterInternal] Должен продолжить без аутентификации при неверном токене")
    void shouldProceedWithoutAuthenticationWithInvalidToken() throws ServletException, IOException {
        String invalidToken = "invalidToken";

        request.addHeader("Authorization", "Bearer " + invalidToken);
        when(jwtUtil.verifyAndGetUserId(invalidToken)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, times(1)).verifyAndGetUserId(invalidToken);
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    @DisplayName("[doFilterInternal] Должен корректно обработать исключение при загрузке пользователя")
    void shouldHandleExceptionWhenLoadingUserDetails() throws ServletException, IOException {
        String validToken = "validToken";
        String email = "test@example.com";

        request.addHeader("Authorization", "Bearer " + validToken);
        when(jwtUtil.verifyAndGetUserId(validToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenThrow(new RuntimeException("User loading error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, times(1)).verifyAndGetUserId(validToken);
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verify(filterChain, times(1)).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Контекст безопасности должен быть пустым");
    }

}
