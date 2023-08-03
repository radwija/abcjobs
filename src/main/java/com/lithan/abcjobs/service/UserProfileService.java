package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.UserProfile;

import java.util.Optional;

public interface UserProfileService {
    void saveUpdateUserProfile(UserProfile userProfile);
    Optional<UserProfile> getUserProfileById(Long userProfileId);
}
