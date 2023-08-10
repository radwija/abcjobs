package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.RegistrationRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUserRegister(RegistrationRequest user);
    void deleteUserByUserId(Long userId);
    List<User> getAllUsers();
    List<User> findUserByRole(String role);
    User getUserByUsername(String username);
    UserProfile getUserProfileByUsername(String username);
    User activateAccount(Long id);
    List<User> searchForUsers(String keyword);
}
