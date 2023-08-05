package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadComment;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;

public interface ThreadCommentService {
    void saveComment(Long threadId, ThreadCommentRequest threadCommentRequest, String username);
}
