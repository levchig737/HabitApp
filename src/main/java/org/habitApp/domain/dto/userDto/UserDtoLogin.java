package org.habitApp.domain.dto.userDto;

/**
 * UserDtoLogin
 */
public class UserDtoLogin{
    private String email;
    private String password;

    /**
     * Конструктор
     * @param email email
     * @param password password
     */
    public UserDtoLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * UserDtoLogin
     */
    public UserDtoLogin(){}


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserDtoRegister{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}