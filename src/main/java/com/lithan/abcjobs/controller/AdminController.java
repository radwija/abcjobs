package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.constraint.EApplyJobStatus;
import com.lithan.abcjobs.entity.*;
import com.lithan.abcjobs.exception.AccountNotFoundException;
import com.lithan.abcjobs.exception.JobApplicationNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.payload.request.SendMailRequest;
import com.lithan.abcjobs.payload.response.JobApplicationResponse;
import com.lithan.abcjobs.payload.response.JobResponse;
import com.lithan.abcjobs.repository.JobRepository;
import com.lithan.abcjobs.service.*;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private ThreadPostService threadPostService;

    @Autowired
    private JobRepository jobRepository;

    @GetMapping({"", "/", "/dashboard"})
    public ModelAndView mantap(Model model) {
        List<User> users = userService.getAllUsers();
        List<Job> jobs = jobService.getAllJobs();
        List<ApplyJob> jobApplications = applyJobService.getAllAppliedJobs();
        List<ThreadPost> threadPosts = threadPostService.findAllThreadPosts();
        List<ApplyJob> pendingJobApplications = applyJobService.findAppliedJobByStatus(EApplyJobStatus.PENDING.toString());
        List<ApplyJob> acceptedJobApplications = applyJobService.findAppliedJobByStatus(EApplyJobStatus.ACCEPTED.toString());
        List<ApplyJob> declinedJobApplications = applyJobService.findAppliedJobByStatus(EApplyJobStatus.DECLINED.toString());


        model.addAttribute("isInDashboard", true);
        model.addAttribute("userNumber", users.size());
        model.addAttribute("jobNumber", jobs.size());
        model.addAttribute("threadNumber", threadPosts.size());
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

    @GetMapping("/send-mail")
    public ModelAndView sendMailView(Model model, RedirectAttributes redirectAttributes) {
        ModelAndView sendMailPage = new ModelAndView("admin/sendMail");
        SendMailRequest sendMailRequest = new SendMailRequest();
        List<User> users = userService.getAllUsers();
        List<User> adminUsers = userService.findUserByRole("ADMIN");

        for (User adminUser : adminUsers) {
            users.remove(adminUser);
        }

        sendMailPage.addObject("sendMailRequest", sendMailRequest);
        model.addAttribute("users", users);
        return sendMailPage;
    }

    @PostMapping("/sendMail")
    public ModelAndView sendMail(@ModelAttribute SendMailRequest sendMailRequest, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            emailSenderService.sendMailToUsers(sendMailRequest, username);
            redirectAttributes.addFlashAttribute("successMessage", "Email sent successfully!");
            return new ModelAndView("redirect:/admin/send-mail");
        } catch (RefusedActionException e) {
            redirectAttributes.addFlashAttribute("successMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/send-mail");
        }
    }

    @GetMapping("/deleteUser")
    public ModelAndView deleteUser(@RequestParam("userId") String userIdStr, RedirectAttributes redirectAttributes, Model model) {
        try {
            Long userId = Long.parseLong(userIdStr);
            userService.deleteUserByUserId(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User ID: " + userId + " deleted successfully");
            return new ModelAndView("redirect:/admin/user-management");
        } catch (AccountNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/user-management");
        }
    }

    @GetMapping("/jobs")
    public ModelAndView jobListView(@RequestParam(value = "q", required = false) String keyword, @RequestParam(value = "level", required = false) String level, @RequestParam(value = "time", required = false) String time, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView jobListPage = new ModelAndView("admin/admin");
        List<Job> searchedJobs = jobService.getAllJobs();
        if (searchedJobs.size() == 0) {
            model.addAttribute("noResultMessage", "There isn't any jobs");
        }
        if (keyword != null) {
            searchedJobs = jobService.searchForJobs(keyword);
            if (searchedJobs.isEmpty()) {
                model.addAttribute("noResultMessage", "No result for job search: " + keyword);
            }
        } else if (level != null) {
            searchedJobs = jobService.findJobsByJobLevel(level);
            if (searchedJobs.isEmpty()) {
                model.addAttribute("noResultMessage", "No result for job level " + level);
            }
        } else if (time != null) {
            searchedJobs = jobService.findJobsByJobTime(time);
            if (searchedJobs.isEmpty()) {
                model.addAttribute("noResultMessage", "No result for job time " + time);
            }
        }
        model.addAttribute("jobs", searchedJobs);
        model.addAttribute("isInJobs", true);
        return jobListPage;
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
            for (ApplyJob applicant : applicants) {
                applicant.setQualificationSrc("data:image/png;base64," + applicant.getBase64Qualification());
            }
            model.addAttribute("isInPendingTab", true);
            model.addAttribute("applicants", applicants);
        } else if (Objects.equals(tab, "accepted")) {
            applicants = applyJobService.findAppliedJobByStatus(EApplyJobStatus.ACCEPTED.toString());
            for (ApplyJob applicant : applicants) {
                applicant.setQualificationSrc("data:image/png;base64," + applicant.getBase64Qualification());
            }
            model.addAttribute("isInAcceptedTab", true);
            model.addAttribute("applicants", applicants);
        } else if (Objects.equals(tab, "declined")) {
            applicants = applyJobService.findAppliedJobByStatus(EApplyJobStatus.DECLINED.toString());
            for (ApplyJob applicant : applicants) {
                applicant.setQualificationSrc("data:image/png;base64," + applicant.getBase64Qualification());
            }
            model.addAttribute("isInDeclinedTab", true);
            model.addAttribute("applicants", applicants);
        }

        return manageApplicantPage;
    }

    @GetMapping("/jobs/post-job")
    public ModelAndView postJobAdminView(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        boolean isAdmin = userService.getUserByUsername(principal.getName()).getRole().equals("ADMIN");
        if (!isAdmin) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only ADMIN can post jobs!");
            return new ModelAndView("redirect:/jobs");
        }
        ModelAndView postJobAdminPage = new ModelAndView("admin/postJob");
        JobRequest jobRequest = new JobRequest();
        model.addAttribute("heading", "Post new job");
        postJobAdminPage.addObject("jobRequest", jobRequest);
        return postJobAdminPage;
    }

    @GetMapping("/jobs/edit-job")
    public ModelAndView editJobAdminView(@RequestParam("id") String jobIdStr, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        boolean isAdmin = userService.getUserByUsername(principal.getName()).getRole().equals("ADMIN");
        if (!isAdmin) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only ADMIN can post jobs!");
            return new ModelAndView("redirect:/jobs");
        }
        ModelAndView postJobAdminPage = new ModelAndView("admin/postJob");
        Long jobId = Long.parseLong(jobIdStr);
        Job existingJob = jobRepository.findJobByJobId(jobId);
        JobRequest jobRequest = new JobRequest();
        BeanUtils.copyProperties(existingJob, jobRequest);

        model.addAttribute("heading", "Update job");
        postJobAdminPage.addObject("jobRequest", jobRequest);
        return postJobAdminPage;
    }

    @PostMapping("/saveNewJob")
    public ModelAndView saveNewJob(@ModelAttribute JobRequest jobRequest, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return new ModelAndView("redirect:/login");
        }
        try {
            String postByUsername = principal.getName();
            JobResponse response = jobService.saveJob(jobRequest, postByUsername);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/admin/jobs");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/jobs");
        }

    }

    @GetMapping("/deleteJob")
    public ModelAndView deleteJob(@RequestParam("jobId") String jobIdStr, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            Long jobId = Long.parseLong(jobIdStr);
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
    public ModelAndView acceptJobApplication(@RequestParam("applyJobId") String applyJobIdStr, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Long applyJobId = Long.parseLong(applyJobIdStr);
            JobApplicationResponse response = applyJobService.acceptJobApplication(applyJobId);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        } catch (JobApplicationNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        }
    }

    @GetMapping("/declineJobApplication")
    public ModelAndView declineJobApplication(@RequestParam("applyJobId") String applyJobIdStr, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Long applyJobId = Long.parseLong(applyJobIdStr);
            JobApplicationResponse response = applyJobService.declineJobApplication(applyJobId);
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        } catch (JobApplicationNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/admin/jobs/manage-applicant?tab=pending");
        }
    }
}
