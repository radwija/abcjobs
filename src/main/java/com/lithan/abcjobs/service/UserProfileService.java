package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.UserProfile;

public interface UserProfileService {
    void saveUpdateUserProfile(UserProfile userProfile, String Username);
}
