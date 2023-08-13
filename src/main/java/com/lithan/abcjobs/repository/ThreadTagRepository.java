package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.ThreadTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadTagRepository extends JpaRepository<ThreadTag, Long> {
    ThreadTag findByTagId(Long tagId);
    ThreadTag findByTagName(String tagName);
    List<ThreadTag> findAllByTagName(String tagName);
}
