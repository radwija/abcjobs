package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/u/{username}")
    public ModelAndView profileView(@PathVariable String username) {
        User user = userRepository.getUserByUsername(username);
        ModelAndView profilePage = new ModelAndView("user/profile");
        profilePage.addObject("user", user);

        return profilePage;
    }
}
