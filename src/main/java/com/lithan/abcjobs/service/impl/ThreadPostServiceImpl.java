package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.repository.ThreadPostRepository;
import com.lithan.abcjobs.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreadPostServiceImpl implements ThreadService {
    @Autowired
    private ThreadPostRepository threadPostRepository;

    @Override
    public void saveThread(ThreadPost thread) {
        threadPostRepository.save(thread);
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
