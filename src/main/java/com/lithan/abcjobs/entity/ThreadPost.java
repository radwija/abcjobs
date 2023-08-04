package com.lithan.abcjobs.entity;

import javax.persistence.*;

@Entity
public class ThreadPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long threadId;

    private String title;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

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
