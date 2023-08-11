package com.lithan.abcjobs.payload.request;

public class ResetPasswordRequest {
    private String newPassword;

    private String uuid;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
