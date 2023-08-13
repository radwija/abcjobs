package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Education;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.EducationRequest;
import com.lithan.abcjobs.payload.response.EducationResponse;

import java.util.List;

public interface EducationService {
    Education findEducationByEducationId(Long educationId);
    List<Education> findEducationsByUserProfile(UserProfile userProfile);
    EducationResponse saveEducation(EducationRequest educationRequest, String username);
    Education mapEducationRequestToEducation(EducationRequest educationRequest);
    void mapEducationRequestToEducation(EducationRequest educationRequest, Education education);
    void deleteEducation(Long educationId, String usernameDeleter);
}
