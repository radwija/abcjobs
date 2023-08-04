package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadPostRepository extends JpaRepository<ThreadPost, Long> {
    List<ThreadPost> getThreadPostsByUser(User user);
}
