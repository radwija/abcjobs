package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.request.RegistrationRequest;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping({"","/"})
    public ModelAndView handleRootRequest() {
        return new ModelAndView("index");
    }

    @GetMapping("/register")
    public ModelAndView registerView() {
        ModelAndView registrationPage = new ModelAndView("registration");
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationPage.addObject("registrationRequest", registrationRequest);

        return registrationPage;
    }

    @PostMapping("/saveUserRegister")
    public ModelAndView saveUser(@ModelAttribute RegistrationRequest registrationRequest, Model model) {
        try {
            userService.saveUserRegister(registrationRequest);
            return new ModelAndView("index");
        } catch (CredentialAlreadyTakenException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView("registration");
        }
    }


}
