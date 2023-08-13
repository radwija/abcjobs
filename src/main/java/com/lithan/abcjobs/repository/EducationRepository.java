package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.Education;
import com.lithan.abcjobs.entity.Experience;
import com.lithan.abcjobs.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    Education findEducationByEducationId(Long educationId);
    List<Education> findEducationsByUserProfile(UserProfile userProfile);
}
