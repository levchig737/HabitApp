package org.habitApp.domain.dto.userDto;

/**
 * UserDtoRegisterUpdate
 */
public class UserDtoRegisterUpdate {
    private String email;
    private String password;
    private String name;

    /**
     * Конструктор
     * @param email email
     * @param password password
     * @param name name
     */
    public UserDtoRegisterUpdate(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /**
     * Констурктор
     */
    public UserDtoRegisterUpdate(){}


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "UserDtoRegister{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}