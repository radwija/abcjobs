package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.Experience;
import com.lithan.abcjobs.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    Experience findExperienceByExperienceId(Long experienceId);
    List<Experience> findExperiencesByUserProfile(UserProfile userProfile);
}
