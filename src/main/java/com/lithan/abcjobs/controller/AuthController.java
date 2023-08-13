package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.ThreadTag;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.payload.request.RegistrationRequest;
import com.lithan.abcjobs.service.ThreadPostService;
import com.lithan.abcjobs.service.ThreadTagService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private ThreadPostService threadPostService;

    @Autowired
    private ThreadTagService threadTagService;

    @GetMapping({"", "/"})
    public ModelAndView handleRootRequest(Model model, Principal principal, HttpSession httpSession) {
        if (principal == null) {
            return new ModelAndView("index");
        }
        User authenticatedUser = userService.getUserByUsername(principal.getName());
        UserProfile userProfile = userService.getUserProfileByUsername(principal.getName());

        List<ThreadTag> tags = threadTagService.findAllOrderByNumberOfThreadPostsDesc();
        List<ThreadPost> threadPosts = threadPostService.findAllThreadPosts();

        threadPosts.forEach(thread -> thread.setFormattedCreatedAt(formatDate(thread.getCreatedAt())));
        model.addAttribute("threadPosts", threadPosts);
        model.addAttribute("user", authenticatedUser);
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("tags", tags);

        return new ModelAndView("user/userDashboard");
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormat.format(date);
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
    public ModelAndView registerView(@ModelAttribute RegistrationRequest registrationRequest, Principal principal) {
        if (principal == null) {
            ModelAndView registrationPage = new ModelAndView("auth/registration/registration");
            // @ModelAttribute RegistrationRequest registrationRequest is used because to get the object from failed /saveUserRegister
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
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/register");
        }
    }

    @GetMapping("/thank-you")
    public ModelAndView thankYouPageView(Principal principal, Model model) {
        if (principal != null) {
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView("auth/registration/thankYou");
    }

    @GetMapping("/register-confirmation")
    public ModelAndView accountActivation(@RequestParam(name = "confirm") String registrationCode, Model model, Principal principal) {
        try {
            if (principal != null) {
                return new ModelAndView("redirect:/");
            }
            User activatedUser = userService.activateAccount(registrationCode);
            model.addAttribute("activatedEmail", activatedUser.getEmail());
            return new ModelAndView("auth/registration/registerConfirmation");
        } catch (AccountNotFoundException e) {
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

    @PostMapping("/login")
    public ModelAndView loginViewResponse(Principal principal, RedirectAttributes redirectAttributes, @RequestParam String error) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect credential or account not activated");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("redirect:/");
    }
}
