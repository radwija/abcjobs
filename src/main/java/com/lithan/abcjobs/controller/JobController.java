package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.exception.JobNotFoundException;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping("/jobs")
    public ModelAndView jobListView(@RequestParam(value = "q", required = false) String keyword, @ModelAttribute ApplyJobRequest applyJobRequest, Model model) {
        ModelAndView jobListPage = new ModelAndView("job/jobs");
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
    public ModelAndView jobDetailView(@RequestParam(value = "detail", required = false) Long jobId, @ModelAttribute ApplyJobRequest applyJobRequest, Model model, RedirectAttributes redirectAttributes) {
        try {
            ModelAndView jobDetailPage = new ModelAndView("job/jobDetail");
            if (jobId == null) {
                return new ModelAndView("redirect:/jobs");
            }
            Job detailedJob = jobService.findJobByJobId(jobId);
            model.addAttribute("detailedJob", detailedJob);
            return jobDetailPage;
        } catch (JobNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView("redirect:/jobs");
        }
    }
}
