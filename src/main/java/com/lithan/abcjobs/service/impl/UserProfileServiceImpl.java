package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Override
    public UserProfile saveUpdateUserProfile(UserProfile userProfile) {
        UserProfile savedUserProfile = userProfileRepository.findById(userProfile.getUserDetailId()).get();

        savedUserProfile.setFirstName(userProfile.getFirstName());
        savedUserProfile.setLastName(userProfile.getLastName());
        savedUserProfile.setTitle(userProfile.getTitle());
        savedUserProfile.setCity(userProfile.getCity());
        savedUserProfile.setCountry(userProfile.getCountry());

        userProfileRepository.save(savedUserProfile);
        return savedUserProfile;
    }

    @Override
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

}
