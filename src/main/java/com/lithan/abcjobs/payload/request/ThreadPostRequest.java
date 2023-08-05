package com.lithan.abcjobs.payload.request;

import javax.persistence.Lob;

public class ThreadPostRequest {
    private String title;

    @Lob
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
