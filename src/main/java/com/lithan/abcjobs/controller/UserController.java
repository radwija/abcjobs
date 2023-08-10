package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;
import com.lithan.abcjobs.payload.request.ThreadPostRequest;
import com.lithan.abcjobs.service.*;
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

    @Autowired
    private ApplyJobService applyJobService;

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

            boolean isAdminVisible = user.getRole().equals("ADMIN");
            if (principal != null) {
                isAdminVisible = isAdminVisible && !userService.getUserByUsername(principal.getName()).getRole().equals("ADMIN");
            }
            if (isAdminVisible) {
                throw new AccountNotFoundException("User not found");
            }

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
            return new ModelAndView("people/people");
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
        UserProfile savedUserProfile = userProfileService.saveUpdateUserProfile(userProfile);
        String profileUsername = savedUserProfile.getUser().getUsername();

        boolean isAdmin = userService.getUserByUsername(principal.getName()).equals("ADMIN");

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        if (isAdmin) {
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully! (ADMIN)");
        }
        return new ModelAndView("redirect:/u/" + profileUsername);
    }

    @GetMapping("/create-thread")
    public ModelAndView createThreadView(RedirectAttributes redirectAttributes) {
        ModelAndView createThreadPage = new ModelAndView("thread/createThread");

        // Used for creating new thread
        ThreadPostRequest newThreadPost = new ThreadPostRequest();
        createThreadPage.addObject("newThreadPost", newThreadPost);

        return createThreadPage;
    }

    @PostMapping("/saveNewThread")
    public ModelAndView saveNewThread(@ModelAttribute ThreadPostRequest newThreadPost, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            ThreadPost threadPost = threadPostService.saveThread(newThreadPost, username);

            Long newThreadId = threadPost.getThreadId();

            redirectAttributes.addFlashAttribute("successMessage", "New thread created successfully!");
            return new ModelAndView("redirect:/u/" + username + "/thread?id=" + newThreadId);
        } catch (
                RefusedActionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/create-thread");
        }
    }

    @PostMapping("/saveThreadComment")
    public ModelAndView saveThreadComment(@RequestParam("threadId") String threadIdStr, @ModelAttribute ThreadCommentRequest threadCommentRequest, Principal principal, RedirectAttributes redirectAttributes) {
        Long threadId = Long.parseLong(threadIdStr);
        String threadOwnerUsername = threadPostService.getThreadPostByThreadId(threadId).getUser().getUsername();
        try {
            String username = principal.getName();
            threadCommentService.saveComment(threadId, threadCommentRequest, username);
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        } catch (RefusedActionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        }
    }

    @GetMapping("/deleteThread")
    public ModelAndView deleteThread(@RequestParam("threadId") String threadIdStr, Principal principal, RedirectAttributes redirectAttributes) {
        Long threadId = Long.parseLong(threadIdStr);
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

    @PostMapping("/saveUpdatedThread")
    public ModelAndView saveUpdatedThread(@ModelAttribute ThreadPost threadPost, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        Long threadId = threadPost.getThreadId();
        String threadOwnerUsername = threadPostService.getThreadPostByThreadId(threadId).getUser().getUsername();
        if (principal == null) {
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        }

        String authUsername = principal.getName();
        boolean isAdmin = userService.getUserByUsername(authUsername).getUsername().equals("ADMIN");

        try {
            threadPostService.saveUpdateThreadPost(threadPost, authUsername);
            model.addAttribute("successMessage", "Thread edited successfully!");
            if (isAdmin) {
                redirectAttributes.addFlashAttribute("successMessage", "Thread edited successfully! (ADMIN)");
            }
            redirectAttributes.addFlashAttribute("successMessage", "Thread edited successfully!");
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        } catch (RefusedActionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        }
    }

    @GetMapping("/people")
    public ModelAndView viewAllThreadsView(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<User> users = userService.getAllUsers();
        List<User> adminUsers = userService.findUserByRole("ADMIN");

        if (users.size() == 0) {
            model.addAttribute("noResultMessage", "There isn't any thread");
        }
        if (keyword != null) {
            users = userService.searchForUsers(keyword);
        }

        for (User adminUser : adminUsers) {
            users.remove(adminUser);
        }
        System.out.println();
        if (users.size() == 0 && keyword != null) {
            model.addAttribute("noResultMessage", "No search result for: " + keyword);
        }
        model.addAttribute("users", users);
        return new ModelAndView("people/people");
    }

    @PostMapping("/applyJob")
    public ModelAndView applyJob(@RequestParam("detail") String jobIdStr, Principal principal, @ModelAttribute ApplyJobRequest applyJobRequest, RedirectAttributes redirectAttributes) {
        Long jobId = Long.parseLong(jobIdStr);
        try {
            String appliedByUsername = principal.getName();
            applyJobService.saveAppliedJob(jobId, applyJobRequest, appliedByUsername);
            redirectAttributes.addFlashAttribute("successMessage", "Job applied successfully!");
            return new ModelAndView("redirect:/job/applied");
        } catch (RefusedActionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/job?detail=" + jobId);
        }
    }
}
