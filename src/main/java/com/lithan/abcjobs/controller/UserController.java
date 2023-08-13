package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.*;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.*;
import com.lithan.abcjobs.payload.response.EducationResponse;
import com.lithan.abcjobs.payload.response.ExperienceResponse;
import com.lithan.abcjobs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EducationService educationService;

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

                List<Experience> experiences = experienceService.findExperiencesByUserProfile(userProfile);
                experiences.forEach(experience -> experience.setFormattedStartDate(formatDate(experience.getStartDate())));
                experiences.forEach(experience -> experience.setFormattedEndDate(formatDate(experience.getStartDate())));
                model.addAttribute("experiences", experiences);

                List<Education> educations = educationService.findEducationsByUserProfile(userProfile);
                educations.forEach(education -> education.setFormattedStartDate(formatDate(education.getStartDate())));
                educations.forEach(education -> education.setFormattedEndDate(formatDate(education.getStartDate())));
                model.addAttribute("educations", educations);
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
        model.addAttribute("buttonText", "Add new experience");
        return addExperiencePage;
    }

    @GetMapping("/edit-experience")
    public ModelAndView editExperienceView(@RequestParam("id") String experienceIdStr, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView addExperiencePage = new ModelAndView("user/addExperience");
        Long experienceId = Long.parseLong(experienceIdStr);

        Experience existingExperience = experienceService.findExperienceByExperienceId(experienceId);
        if (existingExperience == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Experience not found!");
            return new ModelAndView("redirect:/");
        }

        String ownerUsername = existingExperience.getUserProfile().getUser().getUsername();
        String currentUsername = principal.getName();
        boolean isOwner = currentUsername.equals(ownerUsername);
        if (!isOwner) {
            redirectAttributes.addFlashAttribute("errorMessage", "You're not allowed to edit the experience!");
            return new ModelAndView("redirect:/u/" + ownerUsername);
        }
        ExperienceRequest experienceRequest = new ExperienceRequest();
        experienceRequest.setExperienceId(existingExperience.getExperienceId());
        experienceRequest.setExperienceName(existingExperience.getExperienceName());
        experienceRequest.setCompanyName(existingExperience.getCompanyName());
        experienceRequest.setStartDate(existingExperience.getStartDate());
        experienceRequest.setEndDate(existingExperience.getEndDate());

        addExperiencePage.addObject("experienceRequest", experienceRequest);
        model.addAttribute("heading", "Edit experience");
        model.addAttribute("buttonText", "Save update");
        return addExperiencePage;
    }

    @PostMapping("/saveExperience")
    public ModelAndView saveExperience(@ModelAttribute ExperienceRequest experienceRequest, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            if (principal == null) {
                return new ModelAndView("redirect:/login");
            }
            String username = principal.getName();
            ExperienceResponse response = experienceService.saveExperience(experienceRequest, username);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/u/" + username);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/deleteExperience")
    public ModelAndView deleteExperience(@RequestParam("id") String experienceIdStr, Principal principal, RedirectAttributes redirectAttributes) {
        Long experienceId = Long.parseLong(experienceIdStr);
        String ownerUsername = experienceService.findExperienceByExperienceId(experienceId).getUserProfile().getUser().getUsername();
        if (principal == null) {
            return new ModelAndView("redirect:/u/" + ownerUsername + "?tab=profile");
        }
        try {
            String usernameDeleter = principal.getName();
            experienceService.deleteExperience(experienceId, usernameDeleter);

            redirectAttributes.addFlashAttribute("successMessage", "Experience ID: " + experienceId + " deleted successfully!");
        } catch (RefusedActionException e) {
            System.out.println(e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/u/" + ownerUsername + "?tab=profile");
    }

    @GetMapping("/add-education")
    public ModelAndView addEducationView(Model model) {
        ModelAndView addExperiencePage = new ModelAndView("user/addEducation");

        EducationRequest educationRequest = new EducationRequest();
        addExperiencePage.addObject("educationRequest", educationRequest);
        model.addAttribute("heading", "Add new education");
        model.addAttribute("buttonText", "Add new education");
        return addExperiencePage;
    }

    @GetMapping("/edit-education")
    public ModelAndView editEducationView(@RequestParam("id") String educationIdStr, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView addExperiencePage = new ModelAndView("user/addEducation");
        Long educationId = Long.parseLong(educationIdStr);

        Education existingEducation = educationService.findEducationByEducationId(educationId);
        if (existingEducation == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Education not found!");
            return new ModelAndView("redirect:/");
        }

        String ownerUsername = existingEducation.getUserProfile().getUser().getUsername();
        String currentUsername = principal.getName();
        boolean isOwner = currentUsername.equals(ownerUsername);
        if (!isOwner) {
            redirectAttributes.addFlashAttribute("errorMessage", "You're not allowed to edit the education!");
            return new ModelAndView("redirect:/u/" + ownerUsername);
        }
        EducationRequest educationRequest = new EducationRequest();
        educationRequest.setEducationId(existingEducation.getEducationId());
        educationRequest.setEducationName(existingEducation.getEducationName());
        educationRequest.setInstitutionName(existingEducation.getInstitutionName());
        educationRequest.setStartDate(existingEducation.getStartDate());
        educationRequest.setEndDate(existingEducation.getEndDate());

        addExperiencePage.addObject("educationRequest", educationRequest);
        model.addAttribute("heading", "Edit education");
        model.addAttribute("buttonText", "Save update");
        return addExperiencePage;
    }

    @PostMapping("/saveEducation")
    public ModelAndView saveEducation(@ModelAttribute EducationRequest educationRequest, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            if (principal == null) {
                return new ModelAndView("redirect:/login");
            }
            String username = principal.getName();
            EducationResponse response = educationService.saveEducation(educationRequest, username);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/u/" + username);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/deleteEducation")
    public ModelAndView deleteEducation(@RequestParam("id") String educationIdStr, Principal principal, RedirectAttributes redirectAttributes) {
        Long educationId = Long.parseLong(educationIdStr);
        String ownerUsername = educationService.findEducationByEducationId(educationId).getUserProfile().getUser().getUsername();
        if (principal == null) {
            return new ModelAndView("redirect:/u/" + ownerUsername + "?tab=profile");
        }
        try {
            String usernameDeleter = principal.getName();
            educationService.deleteEducation(educationId, usernameDeleter);

            redirectAttributes.addFlashAttribute("successMessage", "Education ID: " + educationId + " deleted successfully!");
        } catch (RefusedActionException e) {
            System.out.println(e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/u/" + ownerUsername + "?tab=profile");
    }

}
