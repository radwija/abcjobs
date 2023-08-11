package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.ERole;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.UserProfileNotFoundException;
import com.lithan.abcjobs.payload.request.ForgotPassworRequest;
import com.lithan.abcjobs.repository.ApplyJobRepository;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.payload.request.RegistrationRequest;
import com.lithan.abcjobs.service.EmailSenderService;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    ApplyJobRepository applyJobRepository;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void saveUserRegister(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername()) && userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CredentialAlreadyTakenException("Username not available and email already taken");
        }
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new CredentialAlreadyTakenException("Username not available");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new CredentialAlreadyTakenException("Email already taken");
        }

        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(ERole.USER.toString());
        user.setActive(false);

        UUID registrationCode = UUID.randomUUID();
        user.setRegistrationCode(registrationCode.toString());

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(registrationRequest.getFirstName());
        userProfile.setLastName(registrationRequest.getLastName());

        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        userRepository.save(user);

        emailSenderService.sendMail(user.getEmail(),
                "Account Activation | ABC Jobs Portal",
                "Thanks for registering in ABC Jobs Portal. Here is you activation URL to get started your journey in ABC Jobs Portal!" +
                        "\n" +
                        "http://localhost:8080/register-confirmation?confirm=" + user.getRegistrationCode().toString()
        );
    }

    @Override
    public void deleteUserByUserId(Long userId) {
        User user = userRepository.getUserByUserId(userId);
        if (user == null) {
            throw new AccountNotFoundException("User ID: " + userId + "  not found");
        }
        if (user.getRole().equals("ADMIN")) {
            throw new AccountNotFoundException("Admin user unable to be deleted!");
        }

        UserProfile userProfile = user.getUserProfile();
        userProfile.setJob(null);
        userProfileService.saveUpdateUserProfile(userProfile);

        applyJobRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findUserByRole(String role) {
        return userRepository.findUserByRole(role);
    }

    @Override
    public List<User> findAllUsersByUserId(String[] userIds) {
        List<Long> ids = new ArrayList<>();

        for (String id : userIds) {
            ids.add(Long.parseLong(id));
        }

        return userRepository.findAllById(ids);
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new AccountNotFoundException("User not found");
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
    public User activateAccount(String registrationCode) {
        User activatedUser = userRepository.findByRegistrationCode(registrationCode);
        System.out.println(activatedUser);
        if (activatedUser != null) {
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

    @Override
    public void updateUuidResetPassword(ForgotPassworRequest forgotPassworRequest) {
        String email = forgotPassworRequest.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AccountNotFoundException("Account not found with email " + email);
        }
        String fullName = user.getUserProfile().getFirstName() + " " + user.getUserProfile().getLastName();

        UUID updatedUuid = UUID.randomUUID();
        String parsedUuid = updatedUuid.toString();
        user.setRegistrationCode(parsedUuid);
        userRepository.save(user);

        String emailContent =
                "Dear " + fullName + ",\n\n" +
                "We recently received a request to reset your password for your ABC Jobs Portal account. To proceed with resetting your password, please click the link below:\n\n" +
                "http://localhost:8080/reset-password?reset=" + parsedUuid + "\n\n" +
                "If you didn't initiate this request, you can safely ignore this email and your password will remain unchanged.\n\n" +
                "Thank you for using ABC Jobs Portal!\n\n" +
                "Best Regards,\n" +
                "The ABC Jobs Portal Team";

        emailSenderService.sendMail(
                email,
                "Reset Password Link | ABC Jobs Portal",
                emailContent
        );
    }

    @Override
    public User findByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public User findByRegistrationCode(String uuid) {
        return userRepository.findByRegistrationCode(uuid);
    }

    @Override
    public void updatePassword(String uuid, String newPassword) {
        User user = userRepository.findByRegistrationCode(uuid);
        if (user == null) {
            throw new AccountNotFoundException("Account not found!");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        UUID updatedUuid = UUID.randomUUID();
        user.setPassword(encodedPassword);
        userRepository.save(user);
        user.setRegistrationCode(updatedUuid.toString());
        userRepository.save(user);
    }
}
