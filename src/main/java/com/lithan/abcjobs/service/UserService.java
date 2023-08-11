package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.RegistrationRequest;

import java.util.List;

public interface UserService {
    void saveUserRegister(RegistrationRequest user);
    void deleteUserByUserId(Long userId);
    List<User> getAllUsers();
    List<User> findUserByRole(String role);
    List<User> findAllUsersByUserId(String[] userIds);
    User getUserByUsername(String username);
    UserProfile getUserProfileByUsername(String username);
    User activateAccount(String registrationCode);
    List<User> searchForUsers(String keyword);
}
