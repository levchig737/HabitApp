package org.habitApp.auth;

import lombok.Getter;
import lombok.Setter;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UnauthorizedAccessException;

/**
 * Контекст авторизации пользователя
 */
public final class AuthInMemoryContext {
    private final static AuthInMemoryContext CONTEXT = new AuthInMemoryContext();

    private UserEntity authenticatedUser;
    @Setter
    @Getter
    private String ip;

    public static synchronized AuthInMemoryContext getContext() {
        return CONTEXT;
    }

    public UserEntity getAuthentication() {
        if (authenticatedUser == null) {
            throw new UnauthorizedAccessException("Unauthorized");
        }
        return authenticatedUser;
    }

    public void setAuthentication(UserEntity user) {
        authenticatedUser = user;
    }
}
