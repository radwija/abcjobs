package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.JobNotFoundException;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.service.ApplyJobService;
import com.lithan.abcjobs.service.JobService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplyJobService applyJobService;

    @GetMapping("/jobs")
    public ModelAndView jobListView(@RequestParam(value = "q", required = false) String keyword, @RequestParam(value = "level", required = false) String level, @RequestParam(value = "time", required = false) String time, @ModelAttribute ApplyJobRequest applyJobRequest, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView jobListPage = new ModelAndView("job/jobs");
        boolean isAdmin = false;
        if (principal != null) {
            isAdmin = userService.getUserByUsername(principal.getName()).getRole().equals("ADMIN");
            if (isAdmin) {
                if (keyword != null) {
                    return new ModelAndView("redirect:/admin/jobs?q=" + keyword);
                } else if (level != null) {
                    return new ModelAndView("redirect:/admin/jobs?level=" + level);
                } else if (time != null) {
                    return new ModelAndView("redirect:/admin/jobs?time=" + time);
                }
                return new ModelAndView("redirect:/admin/jobs");
            }
            jobListPage = new ModelAndView("user/jobsAuth");
        }
        List<Job> searchedJobs = jobService.getAllJobs();

        if (searchedJobs.isEmpty()) {
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
        model.addAttribute("isInJobLists", true);
        return jobListPage;
    }

    @GetMapping("/job")
    public ModelAndView jobDetailView(@RequestParam(value = "detail", required = false) String jobIdStr, @ModelAttribute ApplyJobRequest applyJobRequest, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (jobIdStr.equals("")) {
                return new ModelAndView("redirect:/jobs");
            }
            Long jobId = Long.parseLong(jobIdStr);
            ModelAndView jobDetailPage = new ModelAndView("job/jobDetail");
            Job detailedJob = jobService.findJobByJobId(jobId);
            ApplyJob applyJob = new ApplyJob();
            List<ApplyJob> jobApplications = new ArrayList<>();
            User user = new User();
            boolean isUserAlreadyApply = false;
            boolean isAdmin = false;
            if (principal != null) {
                user = userService.getUserByUsername(principal.getName());
                isAdmin = user.getRole().equals("ADMIN");
                isUserAlreadyApply = applyJobService.checkUserAlreadyApply(user, detailedJob);
            }
            if (isUserAlreadyApply) {
                applyJob = applyJobService.findByAppliedByAndApplyAppliedJob(user, detailedJob);
                applyJob.setQualificationSrc(ApplyJob.encodeQualificationInSrcHtml(applyJob.getQualification()));
                model.addAttribute("applyJob", applyJob);
            }
            if (isAdmin) {
                jobApplications = applyJobService.findAppliedJobByAppliedJob(detailedJob);
                model.addAttribute("jobApplications", jobApplications);
            }

            model.addAttribute("isUserAlreadyApply", isUserAlreadyApply);
            model.addAttribute("isAdmin", isAdmin);
            jobDetailPage.addObject("applyJobRequest", applyJobRequest);
            model.addAttribute("detailedJob", detailedJob);
            return jobDetailPage;
        } catch (JobNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/jobs");
        }
    }
}
