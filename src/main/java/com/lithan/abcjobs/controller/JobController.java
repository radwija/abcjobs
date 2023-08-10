package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.exception.JobNotFoundException;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.payload.request.JobRequest;
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
import java.util.List;

@Controller
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @GetMapping("/jobs")
    public ModelAndView jobListView(@RequestParam(value = "q", required = false) String keyword, @ModelAttribute ApplyJobRequest applyJobRequest, Principal principal, Model model) {
        ModelAndView jobListPage = new ModelAndView("job/jobs");
        if (principal != null) {
            if (userService.getUserByUsername(principal.getName()).getRole().equals("ADMIN")) {
                return new ModelAndView("redirect:/admin/jobs");
            }
            jobListPage = new ModelAndView("user/jobsAuth");
        }
        List<Job> searchedJobs = jobService.getAllJobs();

        if (searchedJobs.size() == 0) {
            model.addAttribute("noResultMessage", "There isn't any jobs");
        }
        if (keyword != null) {
            searchedJobs = jobService.searchForJobs(keyword);
        }
        model.addAttribute("jobs", searchedJobs);
        return jobListPage;
    }

    @GetMapping("/job")
    public ModelAndView jobDetailView(@RequestParam(value = "detail", required = false) String jobIdStr, @ModelAttribute ApplyJobRequest applyJobRequest, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (jobIdStr.equals("")) {
                return new ModelAndView("redirect:/jobs");
            }
            Long jobId = Long.parseLong(jobIdStr);
            ModelAndView jobDetailPage = new ModelAndView("job/jobDetail");
            Job detailedJob = jobService.findJobByJobId(jobId);
            jobDetailPage.addObject("applyJobRequest", applyJobRequest);
            model.addAttribute("detailedJob", detailedJob);
            return jobDetailPage;
        } catch (JobNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/jobs");
        }
    }
}
