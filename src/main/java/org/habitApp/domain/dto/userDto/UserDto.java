package org.habitApp.domain.dto.userDto;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;

    public UserDto() {
    }

    public UserDto(UUID id, String email, String password, String name, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
