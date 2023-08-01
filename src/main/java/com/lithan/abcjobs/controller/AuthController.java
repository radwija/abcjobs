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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        ModelAndView registrationPage = new ModelAndView("registration/registration");
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationPage.addObject("registrationRequest", registrationRequest);

        return registrationPage;
    }

    @PostMapping("/saveUserRegister")
    public ModelAndView saveUser(@ModelAttribute RegistrationRequest registrationRequest, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUserRegister(registrationRequest);

            String emailToActivate = registrationRequest.getEmail();
            redirectAttributes.addFlashAttribute("emailToActivate", emailToActivate);
            return new ModelAndView("redirect:/thank-you");
        } catch (CredentialAlreadyTakenException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView("registration/registration");
        }
    }

    @GetMapping("/thank-you")
    public ModelAndView thankYouPageView(Model model) {
        return new ModelAndView("registration/thank-you");
    }


}
