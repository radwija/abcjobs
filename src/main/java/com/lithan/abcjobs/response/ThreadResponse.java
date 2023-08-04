package com.lithan.abcjobs.response;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;

public class ThreadResponse {
    private User user = null;
    private ThreadPost threadPost = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ThreadPost getThreadPost() {
        return threadPost;
    }

    public void setThreadPost(ThreadPost threadPost) {
        this.threadPost = threadPost;
    }
}
