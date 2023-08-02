package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping("/u/{username}")
    public String profileView(@PathVariable String username, Model model) {
        try {
            User user = userService.getUserByUsername(username);
            UserProfile userProfile = userService.getUserProfileByUsername(username);
            System.out.println("First name: " + userProfile.getFirstName());
            model.addAttribute("user", user);
            model.addAttribute("userProfile", userProfile);
            return "user/profile";
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "exception/userNotFound";
        }
    }
}
