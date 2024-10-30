package org.habitApp.domain.dto.userDto;

import java.util.UUID;

/**
 * UserDto
 */
public class UserDto {
    private UUID id;
    private String email;
    private String password;
    private String name;
    private boolean flagAdmin;

    /**
     * Конструктор UserDto
     * @param id id
     * @param email email
     * @param password password
     * @param name name
     * @param flagAdmin flagAdmin
     */
    public UserDto(UUID id, String email, String password, String name, boolean flagAdmin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.flagAdmin = flagAdmin;
    }

    /**
     * Конструктор
     */
    public UserDto(){}

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + flagAdmin +
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

    public void setFlagAdmin(boolean flagAdmin) {
        this.flagAdmin = flagAdmin;
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

    public boolean isFlagAdmin() {
        return flagAdmin;
    }
}