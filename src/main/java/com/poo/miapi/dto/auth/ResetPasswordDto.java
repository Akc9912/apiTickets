package com.poo.miapi.dto.auth;

public class ResetPasswordDto {
    private Long userId;

    public ResetPasswordDto() {
    }

    public ResetPasswordDto(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
