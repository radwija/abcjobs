package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileService {
    UserProfile saveUpdateUserProfile(UserProfile userProfile);
    List<UserProfile> getAllUserProfiles();
    List<UserProfile> findUserProfileByAppliedJobId(Long jobId);
}
