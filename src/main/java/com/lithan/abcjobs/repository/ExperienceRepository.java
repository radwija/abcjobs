package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    Experience findExperienceByExperienceId(Long experienceId);
}
