package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.*;
import com.lithan.abcjobs.service.*;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private ExperienceService experienceService;

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

    @GetMapping("/forgot-password")
    public ModelAndView forgotPasswordView(Model model) {
        ModelAndView forgotPasswordPage = new ModelAndView("auth/resetPassword/forgotPassword");
        ForgotPassworRequest forgotPasswordRequest = new ForgotPassworRequest();
        forgotPasswordPage.addObject("forgotPasswordRequest", forgotPasswordRequest);

        return forgotPasswordPage;
    }

    @PostMapping("/findAccountByEmail")
    public ModelAndView findAccountByEmail(@ModelAttribute ForgotPassworRequest forgotPassworRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUuidResetPassword(forgotPassworRequest);
            redirectAttributes.addFlashAttribute("emailSent", forgotPassworRequest.getEmail());
            return new ModelAndView("redirect:/email-sent");
        } catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/forgot-password");
        }
    }

    @GetMapping("/email-sent")
    public ModelAndView emailSentView(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            return new ModelAndView("redirect:/");
        }

        return new ModelAndView("auth/resetPassword/emailSent");
    }

    @GetMapping("/reset-password")
    public ModelAndView resetPasswordView(@RequestParam("reset") String uuid, Model model) {
        ModelAndView resetPasswordPage = new ModelAndView("auth/resetPassword/resetPassword");
        User user = userService.findByRegistrationCode(uuid);
        if (user == null) {
            model.addAttribute("isUrlValid", false);
        } else {
            model.addAttribute("isUrlValid", true);
        }

        model.addAttribute("token", uuid);
        return resetPasswordPage;
    }

    @PostMapping("/resetPassword")
    public ModelAndView resetPassword(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            String uuid = request.getParameter("token");
            String newPassword = request.getParameter("password");
            userService.updatePassword(uuid, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
            return new ModelAndView("redirect:/login");
        } catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/add-experience")
    public ModelAndView addExperienceView(Model model) {
        ModelAndView addExperiencePage = new ModelAndView("user/addExperience");

        ExperienceRequest experienceRequest = new ExperienceRequest();
        addExperiencePage.addObject("experienceRequest", experienceRequest);
        model.addAttribute("heading", "Add new experience");
        return addExperiencePage;
    }

    @PostMapping("/saveExperience")
    public ModelAndView saveExperience(@ModelAttribute ExperienceRequest experienceRequest, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            if (principal == null) {
                return new ModelAndView("redirect:/login");
            }
            String username = principal.getName();
            experienceService.saveExperience(experienceRequest, username);
            return new ModelAndView("redirect:/u/" + username);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/");
        }
    }

}
