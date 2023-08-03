package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.request.RegistrationRequest;
import com.lithan.abcjobs.request.UpdateUserProfileRequest;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping({"/u", "/u/"})
    public String peopleView() {
        return "redirect:/people";
    }

    @GetMapping("/u/{username}")
    public String profileView(@PathVariable String username, Model model) {
        try {
            User user = userService.getUserByUsername(username);
            UserProfile userProfile = userService.getUserProfileByUsername(username);
            UpdateUserProfileRequest updateUserProfileRequest = new UpdateUserProfileRequest();

            model.addAttribute("user", user);
            model.addAttribute("userProfile", userProfile);
            return "user/profile";
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "exception/userNotFound";
        }
    }

    @PostMapping("/saveUpdateUserProfile")
    public ModelAndView saveUpdateUserProfile(@ModelAttribute("userProfile") UserProfile userProfile, Principal principal, Model model) {
        String username = principal.getName();
        System.out.println("username controller: " + username);

        userService.saveUpdateUserProfile(userProfile, username);
        model.addAttribute("successMessage", "Profile updated successfully!");
        return new ModelAndView("redirect:/u/" + principal.getName());

    }

}
