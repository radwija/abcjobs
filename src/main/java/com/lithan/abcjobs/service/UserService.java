package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUserRegister(RegistrationRequest user);
    List<User> getAllUsers();
    User getUserByUsername(String username);
    UserProfile getUserProfileByUsername(String username);
    User activateAccount(Long id);
    Optional<User> getUserById(Long userId);
    List<User> searchForUsers(String keyword);
}
