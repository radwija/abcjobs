package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.service.ApplyJobService;
import com.lithan.abcjobs.service.JobService;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplyJobService applyJobService;

    @GetMapping({"", "/", "/dashboard"})
    public ModelAndView mantap(Model model) {
        List<User> users = userService.getAllUsers();
        List<UserProfile> userProfiles = userProfileService.getAllUserProfiles();

        model.addAttribute("users", users);
        model.addAttribute("userProfiles", userProfiles);
        return new ModelAndView("admin/admin");
    }

    @GetMapping("/user-management")
    public ModelAndView userManagementView(@RequestParam(value = "q", required = false) String keyword, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView userManagementPage = new ModelAndView("admin/admin");
        List<User> users = userService.getAllUsers();
        List<UserProfile> userProfiles = userProfileService.getAllUserProfiles();

        model.addAttribute("users", users);
        model.addAttribute("userProfiles", userProfiles);
        model.addAttribute("isInUserManagement", true);
        return  userManagementPage;
    }
    @GetMapping("/jobs")
    public ModelAndView jobListView(@RequestParam(value = "q", required = false) String keyword, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView jobListPage = new ModelAndView("admin/admin");
        List<Job> searchedJobs = jobService.getAllJobs();
        if (searchedJobs.size() == 0) {
            model.addAttribute("noResultMessage", "There isn't any jobs");
        }
        if (keyword != null) {
            searchedJobs = jobService.searchForJobs(keyword);
        }
        model.addAttribute("jobs", searchedJobs);
        model.addAttribute("isInJobs", true);
        return  jobListPage;
    }

    @GetMapping("/jobs/post-job")
    public ModelAndView postJobAdminView(@ModelAttribute JobRequest jobRequest, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView postJobAdminPage = new ModelAndView("admin/postJob");
        postJobAdminPage.addObject("jobRequest", jobRequest);
        return postJobAdminPage;
    }

    @GetMapping("/jobs/manage-applicant")
    public ModelAndView manageApplicantView(@RequestParam("tab") String tab, RedirectAttributes redirectAttributes, Model model) {
        ModelAndView manageApplicantPage = new ModelAndView("admin/admin");
        if (tab.equals("pending")) {
            
        } else if (tab.equals("accepted")) {
            
        }

        return manageApplicantPage;
    }

    @PostMapping("/saveNewJob")
    public ModelAndView saveNewJob(@ModelAttribute JobRequest jobRequest, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return new ModelAndView("redirect:/login");
        }
        String postByUsername = principal.getName();
        jobService.saveJob(jobRequest, postByUsername);

        return new ModelAndView("redirect:/admin/jobs");
    }

    @GetMapping("/deleteJob")
    public ModelAndView deleteJob(@RequestParam("jobId") Long jobId, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            String username = principal.getName();
            applyJobService.deleteApplyJobByAppliedJobId(jobId);
            jobService.deleteJob(jobId, username);

            redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
            return new ModelAndView("redirect:/admin/jobs");
        } catch (RefusedActionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/jobs");
        }
    }
}
