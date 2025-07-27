package com.poo.miapi.dto.auth;

public class ChangePasswordDto {

    private int userId;

    private String newPassword;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(int userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
