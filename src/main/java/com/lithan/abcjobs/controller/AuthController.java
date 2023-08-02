package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.request.RegistrationRequest;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping({"","/"})
    public ModelAndView handleRootRequest(Model model, Principal principal) {
        if (principal == null) {
            return new ModelAndView("index");
        }
        model.addAttribute("user", userService.getUserByUsername(principal.getName()));
        return new ModelAndView("user/userDashboard");
    }

    @GetMapping("/register")
    public ModelAndView registerView() {
        ModelAndView registrationPage = new ModelAndView("auth/registration/registration");
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
            return new ModelAndView("auth/registration/registration");
        }
    }

    @GetMapping("/thank-you")
    public ModelAndView thankYouPageView(Model model) {
        return new ModelAndView("auth/registration/thankYou");
    }

    @GetMapping("/register-confirmation")
    public ModelAndView accountActivation(@RequestParam(name = "id") Long id, Model model) {
        try {
            User activatedUser = userService.activateAccount(id);
            model.addAttribute("activatedEmail", activatedUser.getEmail());
            return new ModelAndView("auth/registration/registerConfirmation");
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView("exception/userActivationNotFound");
        }
    }

    @GetMapping("/login")
    public ModelAndView loginView() {
        return new ModelAndView("auth/login/login");
    }
}
