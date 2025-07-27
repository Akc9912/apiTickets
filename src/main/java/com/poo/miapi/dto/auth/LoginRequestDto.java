package com.poo.miapi.dto.auth;

public class LoginRequestDto {
    private String email;
    private String password;

    // Constructor vacío
    public LoginRequestDto() {
    }

    // Constructor completo
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
