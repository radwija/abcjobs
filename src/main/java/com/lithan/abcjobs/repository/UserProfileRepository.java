package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    List<UserProfile> findUserProfileByJob(Job job);
}
