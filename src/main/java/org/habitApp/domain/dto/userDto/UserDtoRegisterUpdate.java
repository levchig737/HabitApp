package org.habitApp.domain.dto.userDto;

import lombok.*;

/**
 * UserDtoRegisterUpdate
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDtoRegisterUpdate {
    private String email;
    private String password;
    private String username;

    @Override
    public String toString() {
        return "UserDtoRegister{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + username + '\'' +
                '}';
    }
}