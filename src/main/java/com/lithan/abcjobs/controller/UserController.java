package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;
import com.lithan.abcjobs.payload.request.ThreadPostRequest;
import com.lithan.abcjobs.service.ThreadCommentService;
import com.lithan.abcjobs.service.ThreadPostService;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ThreadPostService threadPostService;

    @Autowired
    private ThreadCommentService threadCommentService;

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

            if (Objects.equals(tab, "profile") || Objects.equals(tab, null) || Objects.equals(tab, "")) {
                model.addAttribute("isInProfileTab", true);
                model.addAttribute("profileText", "You're in profile tab");
            } else if (Objects.equals(tab, "threads")) {
                model.addAttribute("isInThreadTab", true);
                // Used for retrieving threads that belong to user
                List<ThreadPost> threadPosts = threadPostService.getThreadPostsByUserId(user);
                threadPosts.forEach(thread -> thread.setFormattedCreatedAt(formatDate(thread.getCreatedAt())));
                model.addAttribute("threadPosts", threadPosts);
            }


            model.addAttribute("user", user);
            model.addAttribute("userProfile", userProfile);
            return new ModelAndView("user/profile");
        } catch (AccountNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView("exception/userNotFound");
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormat.format(date);
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
    public ModelAndView saveNewThread(@ModelAttribute ThreadPostRequest newThreadPost, Principal principal) {
        String username = principal.getName();
        ThreadPost threadPost = threadPostService.saveThread(newThreadPost, username);

        // Getting ID for redirecting to the thread
        System.out.println("Thread ID: " + threadPost.getThreadId());
        return new ModelAndView("redirect:/u/" + username + "?tab=threads");
    }

    @PostMapping("/saveThreadComment")
    public ModelAndView saveThreadComment(@RequestParam("threadId") Long threadId, @ModelAttribute ThreadCommentRequest threadCommentRequest, Principal principal) {
        String threadOwnerUsername = threadPostService.getThreadPostByThreadId(threadId).getUser().getUsername();
        String username = principal.getName();

        threadCommentService.saveComment(threadId, threadCommentRequest, username);
        return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
    }

    @GetMapping("/deleteThread")
    public ModelAndView deleteThread(@RequestParam("threadId") Long threadId, Principal principal, RedirectAttributes redirectAttributes) {
        String threadOwnerUsername = threadPostService.getThreadPostByThreadId(threadId).getUser().getUsername();
        if (principal == null) {
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        }
        try {
            String usernameDeleter = principal.getName();
            threadPostService.deleteThread(threadId, usernameDeleter);

            redirectAttributes.addFlashAttribute("successMessage", "Thread deleted successfully!");
            if (userService.getUserByUsername(usernameDeleter).getRole().equals("ADMIN")) {
                redirectAttributes.addFlashAttribute("successMessage", "Thread deleted successfully! (ADMIN)");
            }
        } catch (RefusedActionException e) {
            System.out.println(e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/u/" + threadOwnerUsername + "?tab=threads");
    }
}
