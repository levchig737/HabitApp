package org.habitApp.models;

/**
 * Роль пользователя
 */
public enum Role {
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    Role(String role) {
        this.role = role;
    }

    private final String role;

    @Override
    public String toString() {
        return role;
    }

    public static Role fromString(String roleName) {
        for (Role role : Role.values()) {
            if (role.role.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown period: " + roleName);
    }
}
