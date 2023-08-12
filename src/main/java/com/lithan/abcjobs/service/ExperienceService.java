package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Experience;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.ExperienceRequest;
import com.lithan.abcjobs.payload.response.ExperienceResponse;

public interface ExperienceService {
    Experience findExperienceByExperienceId(Long experienceId);
    ExperienceResponse saveExperience(ExperienceRequest experienceRequest, String username);
    Experience mapExperienceRequestToExperience(ExperienceRequest experienceRequest);
    void mapExperienceRequestToExperience(ExperienceRequest experienceRequest, Experience experience);
}
