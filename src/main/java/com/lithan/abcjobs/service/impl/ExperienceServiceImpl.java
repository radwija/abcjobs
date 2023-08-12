package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.Experience;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ExperienceRequest;
import com.lithan.abcjobs.payload.response.ExperienceResponse;
import com.lithan.abcjobs.repository.ExperienceRepository;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.service.ExperienceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceServiceImpl implements ExperienceService {
    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public Experience findExperienceByExperienceId(Long experienceId) {
        return experienceRepository.findExperienceByExperienceId(experienceId);
    }

    @Override
    public List<Experience> findExperiencesByUserProfile(UserProfile userProfile) {
        return experienceRepository.findExperiencesByUserProfile(userProfile);
    }

    @Override
    public ExperienceResponse saveExperience(ExperienceRequest experienceRequest, String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new AccountNotFoundException("User with username " + username + " not found");
        }
        UserProfile userProfile = userProfileRepository.findUserProfileByUser(user);

        if (experienceRequest.getExperienceId() == null) {
            Experience newExperience = mapExperienceRequestToExperience(experienceRequest);
            newExperience.setUserProfile(userProfile);
            experienceRepository.save(newExperience);

            ExperienceResponse response = new ExperienceResponse();
            response.setExperience(newExperience);
            response.setMessage("New experience added successfully!");
            return response;
        } else {
            Experience existingExperience = experienceRepository.findExperienceByExperienceId(experienceRequest.getExperienceId());
            if (existingExperience == null) {
                throw new RuntimeException("Experience not found!");
            }
            mapExperienceRequestToExperience(experienceRequest, existingExperience);
            experienceRepository.save(existingExperience);

            ExperienceResponse response = new ExperienceResponse();
            response.setExperience(existingExperience);
            response.setMessage("Experience ID: " + experienceRequest.getExperienceId() + " updated successfully!");
            return response;
        }
    }

    @Override
    public Experience mapExperienceRequestToExperience(ExperienceRequest experienceRequest) {
        Experience experience = new Experience();
        BeanUtils.copyProperties(experienceRequest, experience);
        return experience;
    }

    @Override
    public void mapExperienceRequestToExperience(ExperienceRequest experienceRequest, Experience experience) {
        BeanUtils.copyProperties(experienceRequest, experience);
    }

    @Override
    public void deleteExperience(Long experienceId, String usernameDeleter) {
        String threadOwner = experienceRepository.findExperienceByExperienceId(experienceId).getUserProfile().getUser().getUsername();
        if (!(threadOwner.equals(usernameDeleter))) {
            throw new RefusedActionException("You're not allowed to delete the experience!");
        }

        experienceRepository.deleteById(experienceId);
    }
}
