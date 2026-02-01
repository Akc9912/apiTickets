package com.poo.miapi.module.ticket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProcessRefundRequestDto {

    @NotNull(message = "Admin ID is required")
    private Integer adminId;

    @NotNull(message = "Approval status is required")
    private Boolean approve;

    @Size(max = 500, message = "Resolution comment must not exceed 500 characters")
    private String resolutionComment;

    public ProcessRefundRequestDto() {
    }

    public ProcessRefundRequestDto(Integer adminId, Boolean approve, String resolutionComment) {
        this.adminId = adminId;
        this.approve = approve;
        this.resolutionComment = resolutionComment;
    }

    // Getters y Setters
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getResolutionComment() {
        return resolutionComment;
    }

    public void setResolutionComment(String resolutionComment) {
        this.resolutionComment = resolutionComment;
    }
}
