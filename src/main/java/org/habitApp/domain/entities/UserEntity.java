package org.habitApp.domain.entities;

import lombok.*;

/**
 * Модель User
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserEntity {
    private long id; // изменен на long
    private String email;
    private String password;
    private String name;
    private boolean flagAdmin;



    public UserEntity(String email, String password, String name, boolean flagAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.flagAdmin = flagAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + flagAdmin +
                '}';
    }
}
