package com.lithan.abcjobs.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class ThreadTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;
    private String tagName;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<ThreadPost> threadPosts;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<ThreadPost> getThreadPosts() {
        return threadPosts;
    }

    public void setThreadPosts(List<ThreadPost> threadPosts) {
        this.threadPosts = threadPosts;
    }
}
