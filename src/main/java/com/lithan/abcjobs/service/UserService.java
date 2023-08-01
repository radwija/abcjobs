package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.request.RegistrationRequest;

import java.util.List;

public interface UserService {
    void saveUserRegister(RegistrationRequest user);

    List<User> getAllUsers();

    User getUserByUsername(String username);

    User activateAccount(Long id);
}
