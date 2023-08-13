package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.ThreadTag;
import com.lithan.abcjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadPostRepository extends JpaRepository<ThreadPost, Long> {
    List<ThreadPost> getThreadPostsByUser(User user);
    List<ThreadPost> findThreadPostsByTag(ThreadTag tag);
    ThreadPost getThreadPostByThreadId(Long threadId);

    @Query(value = "SELECT t FROM ThreadPost t WHERE t.title LIKE '%' || :keyword || '%'"
            + "OR t.content LIKE '%' || :keyword || '%'")
    List<ThreadPost> searchForThreadPostsByTitleAndContent(@Param("keyword") String keyword);
}
