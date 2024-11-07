package org.habitApp.auth;

import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UnauthorizedAccessException;

public final class AuthInMemoryContext {
    private final static AuthInMemoryContext CONTEXT = new AuthInMemoryContext();

    private UserEntity authenticatedUser;
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static synchronized AuthInMemoryContext getContext() {
        return CONTEXT;
    }

    public UserEntity getAuthentication() {
        if (authenticatedUser == null) {
            throw new UnauthorizedAccessException("Ошибка авторизации");
        }
        return authenticatedUser;
    }

    public void setAuthentication(UserEntity user) {
        authenticatedUser = user;
    }
}
