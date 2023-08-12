package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Experience;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.ExperienceRequest;
import com.lithan.abcjobs.payload.response.ExperienceResponse;

import java.util.List;

public interface ExperienceService {
    Experience findExperienceByExperienceId(Long experienceId);
    List<Experience> findExperiencesByUserProfile(UserProfile userProfile);
    ExperienceResponse saveExperience(ExperienceRequest experienceRequest, String username);
    Experience mapExperienceRequestToExperience(ExperienceRequest experienceRequest);
    void mapExperienceRequestToExperience(ExperienceRequest experienceRequest, Experience experience);
    void deleteExperience(Long experienceId, String usernameDeleter);
}
