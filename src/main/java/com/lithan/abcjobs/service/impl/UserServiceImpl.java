package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.ERole;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.request.RegistrationRequest;
import com.lithan.abcjobs.service.EmailSenderService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Override
    public void saveUserRegister(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername()) && userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CredentialAlreadyTakenException("Username and email already taken");
        } else if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new CredentialAlreadyTakenException("Username already taken");
        } else if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CredentialAlreadyTakenException("Email already taken");
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());
        user.setRole(ERole.ROLE_USER.toString());
        user.setActive(false);

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(registrationRequest.getFirstName());
        userProfile.setLastName(registrationRequest.getLastName());

        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        userRepository.save(user);

        emailSenderService.sendActivationLink(user.getEmail(),
                "Account Activation | ABC Jobs Portal",
                "Thanks for registering in ABC Jobs Portal. Here is you activation URL to get started your journey in ABC Jobs Portal!" +
                        "\n" +
                        "http://localhost:8080/register-confirmation?id=" + userRepository.findByEmail(user.getEmail()).getUserId());
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Override
    public User activateAccount(Long userId) {

        User activatedUser = userRepository.findById(userId).get();
        activatedUser.setActive(true);

        userRepository.save(activatedUser);
        return userRepository.findById(userId).get();
    }
}
