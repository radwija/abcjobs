package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Override
    public void saveUpdateUserProfile(UserProfile userProfile, String username) {
//        User user = userRepository.getUserByUsername(username);
        UserProfile savedUserProfile = userProfileRepository.findById(userProfile.getUserDetailId()).get();
//        System.out.println("username service: " + user.getUsername());
//        System.out.println("city service: " + savedUserProfile.getCity());

        savedUserProfile.setFirstName(userProfile.getFirstName());
        savedUserProfile.setLastName(userProfile.getLastName());
        savedUserProfile.setTitle(userProfile.getTitle());
        savedUserProfile.setCity(userProfile.getCity());
        savedUserProfile.setCountry(userProfile.getCountry());

        userProfileRepository.save(savedUserProfile);
    }
}
