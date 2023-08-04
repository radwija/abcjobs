package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;

import java.util.List;

public interface ThreadService {
    void saveThread(ThreadPost thread);
    ThreadPost getThreadByThreadId(Long threadId);
    List<ThreadPost> getThreadPostsByUserId(User user);
}
