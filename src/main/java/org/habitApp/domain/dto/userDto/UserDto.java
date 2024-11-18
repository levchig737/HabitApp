package org.habitApp.domain.dto.userDto;

import lombok.*;
import org.habitApp.models.Role;

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
    private String username;
    private Role role;

    public UserDto(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.username = name;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}