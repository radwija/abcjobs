package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Experience;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.ExperienceRequest;

public interface ExperienceService {
    Experience findExperienceByExperienceId(Long experienceId);
    void saveExperience(ExperienceRequest experienceRequest, String username);
    Experience mapExperienceRequestToExperience(ExperienceRequest experienceRequest);
    void mapExperienceRequestToExperience(ExperienceRequest experienceRequest, Experience experience);
}
