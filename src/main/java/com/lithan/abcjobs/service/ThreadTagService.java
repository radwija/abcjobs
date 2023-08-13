package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.ThreadTag;

import java.util.List;

public interface ThreadTagService {
    List<ThreadTag> findAllThreadTags();
    List<ThreadTag> findThreadTagsByTagName(String tagName);
    ThreadTag findThreadTagByTagName(String tagName);
    List<ThreadTag> findAllOrderByNumberOfThreadPostsDesc();
}
