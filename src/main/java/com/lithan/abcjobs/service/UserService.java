package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.request.UserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    void saveUser(UserRequest user);

    List<User> getAllUsers();
}
