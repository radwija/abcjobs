package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.entity.Education;
import com.lithan.abcjobs.entity.Education;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.EducationRequest;
import com.lithan.abcjobs.payload.response.EducationResponse;
import com.lithan.abcjobs.repository.EducationRepository;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.service.EducationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationServiceImpl implements EducationService {
    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public Education findEducationByEducationId(Long educationId) {
        return educationRepository.findEducationByEducationId(educationId);
    }

    @Override
    public List<Education> findEducationsByUserProfile(UserProfile userProfile) {
        return educationRepository.findEducationsByUserProfile(userProfile);
    }

    @Override
    public EducationResponse saveEducation(EducationRequest educationRequest, String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new AccountNotFoundException("User with username " + username + " not found");
        }
        UserProfile userProfile = userProfileRepository.findUserProfileByUser(user);

        if (educationRequest.getEducationId() == null) {
            Education newEducation = mapEducationRequestToEducation(educationRequest);
            newEducation.setUserProfile(userProfile);
            educationRepository.save(newEducation);

            EducationResponse response = new EducationResponse();
            response.setEducation(newEducation);
            response.setMessage("New education added successfully!");
            return response;
        } else {
            Education existingEducation = educationRepository.findEducationByEducationId(educationRequest.getEducationId());
            if (existingEducation == null) {
                throw new RuntimeException("Education not found!");
            }
            mapEducationRequestToEducation(educationRequest, existingEducation);
            educationRepository.save(existingEducation);

            EducationResponse response = new EducationResponse();
            response.setEducation(existingEducation);
            response.setMessage("Education ID: " + educationRequest.getEducationId() + " updated successfully!");
            return response;
        }
    }

    @Override
    public Education mapEducationRequestToEducation(EducationRequest educationRequest) {
        Education education = new Education();
        BeanUtils.copyProperties(educationRequest, education);
        return education;
    }

    @Override
    public void mapEducationRequestToEducation(EducationRequest educationRequest, Education education) {
        BeanUtils.copyProperties(educationRequest, education);
    }

    @Override
    public void deleteEducation(Long educationId, String usernameDeleter) {
        String threadOwner = educationRepository.findEducationByEducationId(educationId).getUserProfile().getUser().getUsername();
        if (!(threadOwner.equals(usernameDeleter))) {
            throw new RefusedActionException("You're not allowed to delete the education!");
        }

        educationRepository.deleteById(educationId);
    }
}
