package com.lithan.abcjobs.payload.request;

import com.lithan.abcjobs.entity.User;

import java.util.ArrayList;
import java.util.List;

public class SendMailRequest {
    private String[] userIds;
    private String subject;
    private String text;

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
