package org.habitApp.domain.dto.userDto;

import lombok.*;

import java.util.UUID;

/**
 * UserDto
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private long id;
    private String email;
    private String password;
    private String name;
    private boolean flagAdmin;

    public UserDto(String email, String password, String name, boolean flagAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.flagAdmin = flagAdmin;
    }

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
}