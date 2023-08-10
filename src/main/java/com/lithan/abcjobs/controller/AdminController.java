package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.constraint.EApplyJobStatus;
import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.JobApplicationNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.payload.response.JobApplicationResponse;
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
import java.util.Objects;

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
        List<Job> jobs = jobService.getAllJobs();
        List<ApplyJob> jobApplications = applyJobService.getAllAppliedJobs();
        List<ApplyJob> pendingJobApplications = applyJobService.findAppliedJobByStatus(EApplyJobStatus.PENDING.toString());
        List<ApplyJob> acceptedJobApplications = applyJobService.findAppliedJobByStatus(EApplyJobStatus.PENDING.toString());
        List<ApplyJob> declinedJobApplications = applyJobService.findAppliedJobByStatus(EApplyJobStatus.DECLINED.toString());


        model.addAttribute("isInDashboard", true);
        model.addAttribute("userNumber", users.size());
        model.addAttribute("jobNumber", jobs.size());
        model.addAttribute("pendingNumber", pendingJobApplications.size());
        model.addAttribute("acceptedNumber", acceptedJobApplications.size());
        model.addAttribute("declinedNumber", declinedJobApplications.size());
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
        return userManagementPage;
    }

    @GetMapping("/deleteUser")
    public ModelAndView deleteUser(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes, Model model) {
        try {
            userService.deleteUserByUserId(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User ID: " + userId + " deleted successfully");
            return new ModelAndView("redirect:/admin/user-management");
        }catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/user-management");
        }
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
        return jobListPage;
    }

    @GetMapping("/jobs/post-job")
    public ModelAndView postJobAdminView(@ModelAttribute JobRequest jobRequest, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView postJobAdminPage = new ModelAndView("admin/postJob");
        postJobAdminPage.addObject("jobRequest", jobRequest);
        return postJobAdminPage;
    }

    @GetMapping("/jobs/manage-applicant")
    public ModelAndView manageApplicantView(@RequestParam(value = "tab", required = false) String tab, RedirectAttributes redirectAttributes, Model model) {
        ModelAndView manageApplicantPage = new ModelAndView("admin/admin");
        model.addAttribute("isInManageApplicant", true);
        List<ApplyJob> applicants = applyJobService.getAllAppliedJobs();

        if (Objects.equals(tab, null) || Objects.equals(tab, "")) {
            int pending = applyJobService.findAppliedJobByStatus(EApplyJobStatus.PENDING.toString()).size();
            int accepted = applyJobService.findAppliedJobByStatus(EApplyJobStatus.ACCEPTED.toString()).size();
            int declined = applyJobService.findAppliedJobByStatus(EApplyJobStatus.DECLINED.toString()).size();

            model.addAttribute("isInRecap", true);
            model.addAttribute("pendingNumber", pending);
            model.addAttribute("acceptedNumber", accepted);
            model.addAttribute("declinedNumber", declined);
        } else if (Objects.equals(tab, "pending")) {
            applicants = applyJobService.findAppliedJobByStatus(EApplyJobStatus.PENDING.toString());
            model.addAttribute("isInPendingTab", true);
            model.addAttribute("applicants", applicants);
        } else if (Objects.equals(tab, "accepted")) {
            applicants = applyJobService.findAppliedJobByStatus(EApplyJobStatus.ACCEPTED.toString());
            model.addAttribute("isInAcceptedTab", true);
            model.addAttribute("applicants", applicants);
        } else if (Objects.equals(tab, "declined")) {
            applicants = applyJobService.findAppliedJobByStatus(EApplyJobStatus.DECLINED.toString());
            model.addAttribute("isInDeclinedTab", true);
            model.addAttribute("applicants", applicants);
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
            jobService.deleteJob(jobId, username);

            redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
            return new ModelAndView("redirect:/admin/jobs");
        } catch (RefusedActionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/jobs");
        }
    }

    @GetMapping("/acceptJobApplication")
    public ModelAndView acceptJobApplication(@RequestParam("applyJobId") Long applyJobId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            JobApplicationResponse response = applyJobService.acceptJobApplication(applyJobId);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        } catch (JobApplicationNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        }
    }

    @GetMapping("/declineJobApplication")
    public ModelAndView declineJobApplication(@RequestParam("applyJobId") Long applyJobId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            JobApplicationResponse response = applyJobService.declineJobApplication(applyJobId);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        } catch (JobApplicationNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        }
    }
}
