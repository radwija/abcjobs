package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    List<UserProfile> getUserProfilesByJobId(Long jobId);
}
