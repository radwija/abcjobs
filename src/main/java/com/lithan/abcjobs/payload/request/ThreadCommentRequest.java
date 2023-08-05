package com.lithan.abcjobs.payload.request;

import com.lithan.abcjobs.entity.ThreadComment;
import com.lithan.abcjobs.entity.ThreadPost;

import javax.persistence.Lob;

public class ThreadCommentRequest {
    private ThreadPost threadPost;
    private String commentMessage;

    public ThreadPost getThreadPost() {
        return threadPost;
    }

    public void setThreadPost(ThreadPost threadPost) {
        this.threadPost = threadPost;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }
}
