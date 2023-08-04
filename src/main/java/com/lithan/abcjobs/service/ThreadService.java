package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.request.ThreadPostRequest;

import java.util.List;

public interface ThreadService {
    void saveThread(ThreadPostRequest thread, String username);
    ThreadPost getThreadByThreadId(Long threadId);
    List<ThreadPost> getThreadPostsByUserId(User user);
}
