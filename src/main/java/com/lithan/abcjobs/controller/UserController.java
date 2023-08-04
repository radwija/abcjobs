package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.CredentialAlreadyTakenException;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.request.RegistrationRequest;
import com.lithan.abcjobs.request.UpdateUserProfileRequest;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping({"/u", "/u/"})
    public String peopleView() {
        return "redirect:/people";
    }

    @GetMapping("/u/{username}")
    public String profileView(@PathVariable String username, Model model, @RequestParam(value = "tab",required = false) String tab ) {
        try {
            User user = userService.getUserByUsername(username);
            UserProfile userProfile = userService.getUserProfileByUsername(username);
            UpdateUserProfileRequest updateUserProfileRequest = new UpdateUserProfileRequest();
            if (Objects.equals(tab, "profile") || tab == null) {
                model.addAttribute("profileText", "You're in profile tab");
            } else if (Objects.equals(tab, "threads")) {
                model.addAttribute("threadText", "You're in thread tab");
            }
            model.addAttribute("user", user);
            model.addAttribute("userProfile", userProfile);
            return "user/profile";
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "exception/userNotFound";
        }
    }

    @PostMapping("/saveUpdateUserProfile")
    public ModelAndView saveUpdateUserProfile(@ModelAttribute("userProfile") UserProfile userProfile, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return new ModelAndView("redirect:/login");
        }
        String profileUsername = userService.getUserById(userProfile.getUserDetailId()).get().getUsername();
        userProfileService.saveUpdateUserProfile(userProfile);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return new ModelAndView("redirect:/u/" + profileUsername);

    }

}
