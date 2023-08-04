package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.ThreadPostNotFoundException;
import com.lithan.abcjobs.exception.UserProfileNotFoundException;
import com.lithan.abcjobs.repository.ThreadPostRepository;
import com.lithan.abcjobs.request.ThreadPostRequest;
import com.lithan.abcjobs.response.ThreadResponse;
import com.lithan.abcjobs.service.ThreadService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThreadPostServiceImpl implements ThreadService {
    @Autowired
    private ThreadPostRepository threadPostRepository;

    @Autowired
    private UserService userService;

    @Override
    public ThreadPost saveThread(ThreadPostRequest thread, String username) {
        User user = userService.getUserByUsername(username);
        ThreadPost savedThread = new ThreadPost();
        savedThread.setTitle(thread.getTitle());
        savedThread.setContent(thread.getContent());
        savedThread.setUser(user);

        threadPostRepository.save(savedThread);
        return savedThread;
    }

    @Override
    public ThreadResponse getThreadByUsernameAndThreadId(String username, Long threadId) {
        User threadOwner = userService.getUserByUsername(username);
        ThreadPost threadPost = threadPostRepository.getThreadPostByThreadId(threadId);
        if (threadOwner == null) {
            throw  new UserProfileNotFoundException("User of this thread not found");
        }
        if (threadPost == null) {
            throw new ThreadPostNotFoundException("Thread not found with ID: " + threadId);
        }

        ThreadResponse threadResponse = new ThreadResponse();
        threadResponse.setUser(threadOwner);
        threadResponse.setThreadPost(threadPost);

        return threadResponse;
    }

    @Override
    public List<ThreadPost> getThreadPostsByUserId(User user) {
        return threadPostRepository.getThreadPostsByUser(user);
    }
}
