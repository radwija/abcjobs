package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.ERole;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.UserProfileNotFoundException;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.payload.request.RegistrationRequest;
import com.lithan.abcjobs.service.EmailSenderService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void saveUserRegister(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername()) && userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CredentialAlreadyTakenException("Username and email already taken");
        } else if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new CredentialAlreadyTakenException("Username already taken");
        } else if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CredentialAlreadyTakenException("Email already taken");
        }

        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(ERole.USER.toString());
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
            throw new AccountNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Override
    public UserProfile getUserProfileByUsername(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null || user.getUserProfile() == null) {
            throw new UserProfileNotFoundException("User profile not found with username: " + username);
        }
        return user.getUserProfile();
    }

    @Override
    public User activateAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User activatedUser = userOptional.get();
            activatedUser.setActive(true);

            userRepository.save(activatedUser);
            return activatedUser;
        }

        throw new AccountNotFoundException("Account not found:(");
    }

    @Override
    public List<User> searchForUsers(String keyword) {
        return userRepository.searchForUsers(keyword);
    }
}
