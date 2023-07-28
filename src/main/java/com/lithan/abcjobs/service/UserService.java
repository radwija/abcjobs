package com.lithan.abcjobs.service;

import com.lithan.abcjobs.request.UserRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void saveUser(UserRequest user);
}
