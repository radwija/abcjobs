package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.repository.ThreadPostRepository;
import com.lithan.abcjobs.request.ThreadPostRequest;
import com.lithan.abcjobs.service.ThreadService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreadPostServiceImpl implements ThreadService {
    @Autowired
    private ThreadPostRepository threadPostRepository;

    @Autowired
    private UserService userService;

    @Override
    public void saveThread(ThreadPostRequest thread, String username) {
        User user = userService.getUserByUsername(username);
        ThreadPost savedThread = new ThreadPost();
        savedThread.setTitle(thread.getTitle());
        savedThread.setContent(thread.getContent());
        savedThread.setUser(user);

        threadPostRepository.save(savedThread);
    }

    @Override
    public ThreadPost getThreadByThreadId(Long threadId) {
        return null;
    }

    @Override
    public List<ThreadPost> getThreadPostsByUserId(User user) {
        return threadPostRepository.getThreadPostsByUser(user);
    }
}
