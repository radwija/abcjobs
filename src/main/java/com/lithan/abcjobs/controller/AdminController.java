package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.service.JobService;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping({"", "/", "/dashboard"})
    public ModelAndView mantap(Model model) {
        List<User> users = userService.getAllUsers();
        List<UserProfile> userProfiles = userProfileService.getAllUserProfiles();

        model.addAttribute("users", users);
        model.addAttribute("userProfiles", userProfiles);
        return new ModelAndView("admin/admin");
    }

    @GetMapping("/post-job")
    public ModelAndView postJobAdminView(@ModelAttribute JobRequest jobRequest, Model model, RedirectAttributes redirectAttributes) {
        ModelAndView postJobAdminPage = new ModelAndView("admin/postJob");
        postJobAdminPage.addObject("jobRequest", jobRequest);
        return postJobAdminPage;
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
}
