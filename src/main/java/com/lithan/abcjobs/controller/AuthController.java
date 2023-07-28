package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.repository.UserRepository;
import com.lithan.abcjobs.request.UserRequest;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public ModelAndView registerView() {
        ModelAndView registrationPage = new ModelAndView("registration");
        UserRequest userRequest = new UserRequest();
        registrationPage.addObject("userRequest", userRequest);

        return registrationPage;
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserRequest userRequest) {
        userService.saveUser(userRequest);

        return "redirect:/mantap";
    }


}
