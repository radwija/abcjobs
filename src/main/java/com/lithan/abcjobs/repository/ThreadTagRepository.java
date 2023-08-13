package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.ThreadTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ThreadTagRepository extends JpaRepository<ThreadTag, Long> {
    ThreadTag findByTagId(Long tagId);
    ThreadTag findByTagName(String tagName);
    List<ThreadTag> findAllByTagName(String tagName);

    @Query("SELECT tt FROM ThreadTag tt LEFT JOIN tt.threadPosts tp GROUP BY tt.tagId ORDER BY COUNT(tp) DESC")

    List<ThreadTag> findAllOrderByNumberOfThreadPostsDesc();
}
