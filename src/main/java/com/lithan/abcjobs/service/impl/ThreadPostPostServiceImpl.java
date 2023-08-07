package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.exception.ThreadPostNotFoundException;
import com.lithan.abcjobs.exception.UserProfileNotFoundException;
import com.lithan.abcjobs.repository.ThreadPostRepository;
import com.lithan.abcjobs.payload.request.ThreadPostRequest;
import com.lithan.abcjobs.payload.response.ThreadResponse;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.service.ThreadPostService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreadPostPostServiceImpl implements ThreadPostService {
    @Autowired
    private ThreadPostRepository threadPostRepository;

    @Autowired
    private UserService userService;

    @Override
    public ThreadPost getThreadPostByThreadId(Long threadId) {
        return threadPostRepository.getThreadPostByThreadId(threadId);
    }

    @Override
    public ThreadPost saveThread(ThreadPostRequest thread, String username) {
        User user = userService.getUserByUsername(username);
        ThreadPost savedThread = new ThreadPost();
        savedThread.setTitle(thread.getTitle());
        savedThread.setContent(thread.getContent());
        savedThread.setUser(user);

        if (user.getRole().equals("ADMIN")) {
            throw new RefusedActionException("Admin user unable to create threads");
        }
        threadPostRepository.save(savedThread);
        return savedThread;
    }

    @Override
    public ThreadResponse getThreadByUsernameAndThreadId(String username, Long threadId) {
        User threadOwner = userService.getUserByUsername(username);
        ThreadPost threadPost = threadPostRepository.getThreadPostByThreadId(threadId);
        if (threadOwner == null) {
            throw new UserProfileNotFoundException("User of this thread not found");
        }
        if (threadPost == null) {
            throw new ThreadPostNotFoundException("Thread not found. Kindly search for another thread.");
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

    @Override
    public void saveUpdateThreadPost(ThreadPost threadPost, String authUsername) {
        ThreadPost savedThreadPost = threadPostRepository.getThreadPostByThreadId(threadPost.getThreadId());
        User authUser = userService.getUserByUsername(authUsername);
        boolean isThreadOwner = authUser.getUsername().equals(savedThreadPost.getUser().getUsername());
        boolean isAdmin = authUser.getRole().equals("ADMIN");

        if (!(isThreadOwner || isAdmin)) {
            throw new RefusedActionException("You're not allowed to edit this thread!");
        }

        savedThreadPost.setTitle(threadPost.getTitle());
        savedThreadPost.setContent(threadPost.getContent());

        threadPostRepository.save(savedThreadPost);
    }

    @Override
    public void deleteThread(Long threadId, String usernameDeleter) {
        String threadOwner = threadPostRepository.getThreadPostByThreadId(threadId).getUser().getUsername();
        boolean isAdmin = userService.getUserByUsername(usernameDeleter).getRole().equals("ADMIN");
        if (!(threadOwner.equals(usernameDeleter) || isAdmin)) {
            throw new RefusedActionException("You're not allowed to delete this thread!");
        }

        threadPostRepository.deleteById(threadId);
    }

    @Override
    public List<ThreadPost> getAllThreadPosts() {
        return threadPostRepository.findAll();
    }

    @Override
    public List<ThreadPost> searchForThreadPostsByTitleAndContent(String keyword) {
        return threadPostRepository.searchForThreadPostsByTitleAndContent(keyword);
    }

}
