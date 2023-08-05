package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.payload.request.ThreadPostRequest;
import com.lithan.abcjobs.payload.response.ThreadResponse;

import java.util.List;

public interface ThreadService {
    ThreadPost saveThread(ThreadPostRequest thread, String username);
    ThreadResponse getThreadByUsernameAndThreadId(String username, Long threadId);
    List<ThreadPost> getThreadPostsByUserId(User user);
}
