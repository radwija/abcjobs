package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.ThreadComment;
import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;
import com.lithan.abcjobs.repository.ThreadCommentRepository;
import com.lithan.abcjobs.service.ThreadCommentService;
import com.lithan.abcjobs.service.ThreadPostService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThreadCommentServiceImpl implements ThreadCommentService {
    @Autowired
    ThreadCommentRepository threadCommentRepository;

    @Autowired
    UserService userService;

    @Autowired
    ThreadPostService threadPostService;

    @Override
    public void saveComment(Long threadId, ThreadCommentRequest threadCommentRequest, String username) {
        User user = userService.getUserByUsername(username);
        ThreadPost threadPost = threadPostService.getThreadPostByThreadId(threadId);
        ThreadComment savedComment = new ThreadComment();

        savedComment.setUser(user);
        savedComment.setThreadPost(threadPost);
        savedComment.setCommentMessage(threadCommentRequest.getCommentMessage());

        threadCommentRepository.save(savedComment);
    }
}
