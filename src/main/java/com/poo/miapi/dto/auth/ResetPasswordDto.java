package com.poo.miapi.dto.auth;

public class ResetPasswordDto {
    private int userId;

    public ResetPasswordDto() {
    }

    public ResetPasswordDto(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
