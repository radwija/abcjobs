package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.constraint.ERole;
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
import java.util.Objects;
import java.util.Optional;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping({"", "/"})
    public ModelAndView handleRootRequest(Model model, Principal principal) {
        if (principal == null) {
            return new ModelAndView("index");
        }
        User authenticatedUser = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", authenticatedUser);

        return new ModelAndView("user/userDashboard");
    }
    @GetMapping("/loginSuccess")
    public String loginSuccessHandle(Model model, Principal principal) {
        // redirecting to login page if user not authenticated
        if (principal == null) {
            return "redirect:/login";
        }

        User authenticatedUser = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", authenticatedUser);
        // redirecting to admin page if user with ROLE_ADMIN as role authenticated
        System.out.println("ROLE: " + authenticatedUser.getRole());
        if (Objects.equals(authenticatedUser.getRole(), "ADMIN")) {
            return "redirect:/admin";
        }

        // redirecting to home page if user with ROLE_USER as role authenticated
        return "redirect:/";
    }

    @GetMapping("/register")
    public ModelAndView registerView(Principal principal) {
        if (principal == null) {
            ModelAndView registrationPage = new ModelAndView("auth/registration/registration");
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationPage.addObject("registrationRequest", registrationRequest);

            return registrationPage;
        }
        return new ModelAndView("redirect:/");
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
    public ModelAndView loginView(Principal principal) {
        if (principal == null) {
            return new ModelAndView("auth/login/login");
        }
        return new ModelAndView("redirect:/");
    }
}
