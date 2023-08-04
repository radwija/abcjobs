package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.UserNotFoundException;
import com.lithan.abcjobs.request.ThreadPostRequest;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import com.lithan.abcjobs.service.impl.ThreadPostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ThreadPostServiceImpl threadPostService;

    @GetMapping({"/u", "/u/"})
    public String peopleView() {
        return "redirect:/people";
    }

    @GetMapping("/u/{username}")
    public ModelAndView profileView(@PathVariable String username, Model model, @RequestParam(value = "tab", required = false) String tab, Principal principal) {
        try {
            ModelAndView profilePage = new ModelAndView("user/profile");

            User user = userService.getUserByUsername(username);
            UserProfile userProfile = userService.getUserProfileByUsername(username);

            if (Objects.equals(tab, "profile") || tab == null) {
                model.addAttribute("isInProfileTab", true);
                model.addAttribute("profileText", "You're in profile tab");
            } else if (Objects.equals(tab, "threads")) {
                model.addAttribute("isInThreadTab", true);
                // Used for retrieving threads that belong to user
                List<ThreadPost> threadPosts = threadPostService.getThreadPostsByUserId(user);
                threadPosts.get(1);
                model.addAttribute("threadPosts", threadPosts);
            }


            model.addAttribute("user", user);
            model.addAttribute("userProfile", userProfile);
            return new ModelAndView("user/profile");
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView("exception/userNotFound");
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

    @GetMapping("/create-thread")
    public ModelAndView createThreadView() {
        ModelAndView createThreadPage = new ModelAndView("thread/createThread");

        // Used for creating new thread
        ThreadPostRequest newThreadPost = new ThreadPostRequest();
        createThreadPage.addObject("newThreadPost", newThreadPost);

        return createThreadPage;
    }

    @PostMapping("/saveNewThread")
    public ModelAndView saveNewThread(@ModelAttribute("userProfile") ThreadPostRequest newThreadPost, Principal principal) {
        String username = principal.getName();
        threadPostService.saveThread(newThreadPost, username);
        return new ModelAndView("redirect:/u/" + username + "?tab=threads");
    }
}
