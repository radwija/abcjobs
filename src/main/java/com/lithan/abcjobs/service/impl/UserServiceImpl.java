package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.ERole;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserDetail;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.repository.UserDetailRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.request.RegistrationRequest;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailRepository userDetailRepository;

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

        UserDetail userDetail = new UserDetail();
        userDetail.setFirstName(registrationRequest.getFirstName());
        userDetail.setLastName(registrationRequest.getLastName());
        userDetail.setUser(user);

        userRepository.save(user);
        userDetailRepository.save(userDetail);

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
}
