package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.UserProfile;

import java.util.List;

public interface UserProfileService {
    UserProfile saveUpdateUserProfile(UserProfile userProfile);
    List<UserProfile> getAllUserProfiles();
    List<UserProfile> findUserProfileByAppliedJob(Job job);
}
