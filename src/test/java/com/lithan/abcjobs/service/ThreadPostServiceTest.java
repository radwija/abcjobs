package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ThreadPostRequest;
import com.lithan.abcjobs.repository.ThreadCommentRepository;
import com.lithan.abcjobs.repository.ThreadPostRepository;
import com.lithan.abcjobs.repository.ThreadTagRepository;
import com.lithan.abcjobs.service.impl.ThreadCommentServiceImpl;
import com.lithan.abcjobs.service.impl.ThreadPostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ThreadPostServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ThreadTagRepository threadTagRepository;

    @Mock
    private ThreadPostRepository threadPostRepository;

    @Mock
    private ThreadCommentRepository threadCommentRepository;

    @InjectMocks
    private ThreadPostServiceImpl threadPostService;

    @InjectMocks
    private ThreadCommentServiceImpl threadCommentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveThreadSuccess() {
        ThreadPostRequest threadRequest = new ThreadPostRequest();
        threadRequest.setTagName("Spring Boot");
        threadRequest.setTitle("New Thread");
        threadRequest.setContent("Content of the thread");

        User user = new User();
        user.setUsername("user123");
        user.setRole("USER");

        when(userService.getUserByUsername("user123")).thenReturn(user);

        ThreadPost savedThread = new ThreadPost();
        savedThread.setTitle("New Thread");
        savedThread.setContent("Content of the thread");
        savedThread.setUser(user);

        when(threadTagRepository.findByTagName("Spring_Boot")).thenReturn(null);
        when(threadPostRepository.save(any(ThreadPost.class))).thenReturn(savedThread);

        ThreadPost result = threadPostService.saveThread(threadRequest, "user123");

        assertNotNull(result);
        assertEquals("New Thread", result.getTitle());
        assertEquals("Content of the thread", result.getContent());
        assertEquals(user, result.getUser());
    }

    @Test
    public void testSaveThreadAdminThrowsException() {
        ThreadPostRequest threadRequest = new ThreadPostRequest();
        threadRequest.setTagName("Spring Boot");
        threadRequest.setTitle("New Thread");
        threadRequest.setContent("Content of the thread");

        User admin = new User();
        admin.setUsername("admin");
        admin.setRole("ADMIN");

        when(userService.getUserByUsername("admin")).thenReturn(admin);

        assertThrows(RefusedActionException.class, () -> {
            threadPostService.saveThread(threadRequest, "admin");
        });
    }

    @Test
    public void testSaveUpdateThreadPostAsOwner() {
        ThreadPostRequest threadRequest = new ThreadPostRequest();
        threadRequest.setThreadId(1L);
        threadRequest.setTagName("Spring Boot");
        threadRequest.setTitle("Updated Thread");
        threadRequest.setContent("Updated content of the thread");

        User ownerUser = new User();
        ownerUser.setUsername("user123");
        ownerUser.setRole("USER");

        when(userService.getUserByUsername("user123")).thenReturn(ownerUser);

        ThreadPost savedThreadPost = new ThreadPost();
        savedThreadPost.setTitle("Original Thread");
        savedThreadPost.setContent("Original content of the thread");
        savedThreadPost.setUser(ownerUser);

        when(threadPostRepository.getThreadPostByThreadId(1L)).thenReturn(savedThreadPost);

        threadPostService.saveUpdateThreadPost(threadRequest, "user123");

        assertEquals("Updated Thread", savedThreadPost.getTitle());
        assertEquals("Updated content of the thread", savedThreadPost.getContent());
    }

    @Test
    public void testSaveUpdateThreadPostAsAdmin() {
        ThreadPostRequest threadRequest = new ThreadPostRequest();
        threadRequest.setThreadId(1L);
        threadRequest.setTagName("Spring Boot");
        threadRequest.setTitle("Updated Thread");
        threadRequest.setContent("Updated content of the thread");

        User adminUser = new User();
        adminUser.setUsername("admin123");
        adminUser.setRole("ADMIN");

        when(userService.getUserByUsername("admin123")).thenReturn(adminUser);

        ThreadPost savedThreadPost = new ThreadPost();
        savedThreadPost.setTitle("Original Thread");
        savedThreadPost.setContent("Original content of the thread");
        savedThreadPost.setUser(new User());

        when(threadPostRepository.getThreadPostByThreadId(1L)).thenReturn(savedThreadPost);

        threadPostService.saveUpdateThreadPost(threadRequest, "admin123");

        assertEquals("Updated Thread", savedThreadPost.getTitle());
        assertEquals("Updated content of the thread", savedThreadPost.getContent());
    }

    @Test
    public void testSaveUpdateThreadPostNotAllowed() {
        ThreadPostRequest threadRequest = new ThreadPostRequest();
        threadRequest.setThreadId(1L);
        threadRequest.setTagName("Spring Boot");
        threadRequest.setTitle("Updated Thread");
        threadRequest.setContent("Updated content of the thread");

        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setRole("USER");

        when(userService.getUserByUsername("otheruser")).thenReturn(otherUser);

        ThreadPost savedThreadPost = new ThreadPost();
        savedThreadPost.setTitle("Original Thread");
        savedThreadPost.setContent("Original content of the thread");
        savedThreadPost.setUser(new User());

        when(threadPostRepository.getThreadPostByThreadId(1L)).thenReturn(savedThreadPost);

        assertThrows(RefusedActionException.class, () -> {
            threadPostService.saveUpdateThreadPost(threadRequest, "otheruser");
        });
    }

    @Test
    public void testDeleteThreadAsOwner() {
        Long threadId = 1L;
        String ownerUsername = "user123";
        User user = new User();
        user.setUsername(ownerUsername);
        user.setRole("USER");

        ThreadPost threadPost = new ThreadPost();
        threadPost.setUser(user);
        threadPost.getUser().setUsername(ownerUsername);

        when(threadPostRepository.getThreadPostByThreadId(threadId)).thenReturn(threadPost);

        when(userService.getUserByUsername(ownerUsername)).thenReturn(threadPost.getUser());

        assertDoesNotThrow(() -> {
            threadPostService.deleteThread(threadId, ownerUsername);
        });
    }

    @Test
    public void testDeleteThreadAsAdmin() {
        // Mocking
        Long threadId = 1L; // Replace with an actual thread ID
        String adminUsername = "admin123";
        User owner = new User();
        owner.setUsername("owner");
        owner.setRole("USER");

        ThreadPost threadPost = new ThreadPost();
        threadPost.setUser(owner);

        when(threadPostRepository.getThreadPostByThreadId(threadId)).thenReturn(threadPost);

        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setRole("ADMIN");
        when(userService.getUserByUsername(adminUsername)).thenReturn(adminUser);

        assertDoesNotThrow(() -> {
            threadPostService.deleteThread(threadId, adminUsername);
        });
    }

    @Test
    public void testDeleteThreadNotAllowed() {
        Long threadId = 1L;
        String otherUsername = "otheruser";

        User owner = new User();
        owner.setUsername("owner");
        owner.setRole("USER");

        ThreadPost threadPost = new ThreadPost();
        threadPost.setUser(owner);

        when(threadPostRepository.getThreadPostByThreadId(threadId)).thenReturn(threadPost);

        User otherUser = new User();
        otherUser.setUsername(otherUsername);
        otherUser.setRole("USER");
        when(userService.getUserByUsername(otherUsername)).thenReturn(otherUser);

        assertThrows(RefusedActionException.class, () -> {
            threadPostService.deleteThread(threadId, otherUsername);
        });
    }


}
