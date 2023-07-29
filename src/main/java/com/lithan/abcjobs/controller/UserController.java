package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.UserNotFoundException;
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

    @GetMapping("/u/{username}")
    public String profileView(@PathVariable String username, Model model) {
        try {
            User user = userService.getUserByUsername(username);
            model.addAttribute("user", user);
            return "user/profile";
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "exception/userNotFound";
        }
    }
}
