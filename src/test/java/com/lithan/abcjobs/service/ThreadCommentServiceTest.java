package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadComment;
import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;
import com.lithan.abcjobs.repository.ThreadCommentRepository;
import com.lithan.abcjobs.service.impl.ThreadCommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ThreadCommentServiceTest {

    @Mock
    private ThreadCommentRepository threadCommentRepository;

    @Mock
    private ThreadPostService threadPostService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ThreadCommentServiceImpl threadCommentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCommentSuccess() {
        Long threadId = 1L;
        String username = "user123";

        ThreadCommentRequest commentRequest = new ThreadCommentRequest();
        commentRequest.setCommentMessage("A new comment");

        User user = new User();
        user.setUsername(username);
        user.setRole("USER");

        User owner = new User();
        user.setUsername("owner");
        user.setRole("USER");

        ThreadPost threadPost = new ThreadPost();
        threadPost.setUser(owner);


        when(userService.getUserByUsername(username)).thenReturn(user);
        when(threadPostService.getThreadPostByThreadId(threadId)).thenReturn(threadPost);
        when(threadCommentRepository.save(any(ThreadComment.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the argument as-is

        ThreadComment savedComment = threadCommentService.saveComment(threadId, commentRequest, username);

        assertNotNull(savedComment);
        assertEquals("A new comment", savedComment.getCommentMessage());
        assertEquals(user, savedComment.getUser());
        assertEquals(threadPost, savedComment.getThreadPost());
    }

    @Test
    public void testSaveCommentAsAdminThrowsException() {
        Long threadId = 1L;
        String adminUsername = "admin123";

        ThreadCommentRequest commentRequest = new ThreadCommentRequest();
        commentRequest.setCommentMessage("A new comment");

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setRole("ADMIN");

        when(userService.getUserByUsername(adminUsername)).thenReturn(admin);

        assertThrows(RefusedActionException.class, () -> {
            threadCommentService.saveComment(threadId, commentRequest, adminUsername);
        });
    }
}
