package org.habitApp.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.services.impl.UserServiceImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Фильтр авторизации пользователя и добавления его в контекст
 */
@Component("authFilter")
public class AuthFilter extends HttpFilter {
    private final UserServiceImpl userService;
    private final JwtUtil jwtUtil;

    public AuthFilter(UserServiceImpl userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        AuthInMemoryContext.getContext().setIp(req.getRemoteAddr());
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            chain.doFilter(req, res);
            return;
        }

        Long userId = checkAuthorization(authHeader);
        if (userId != null) {
            UserEntity userEntity = null;
            try {
                userEntity = userService.findByEmailForAuthentication(userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            AuthInMemoryContext.getContext().setAuthentication(userEntity);
        }

        chain.doFilter(req, res);
        AuthInMemoryContext.getContext().setAuthentication(null);
    }

    private Long checkAuthorization(String authHeader) {
        if (!authHeader.startsWith("Bearer "))
            return null;

        String token = authHeader.substring(7);
        return jwtUtil.verifyAndGetUserId(token);
    }
}
