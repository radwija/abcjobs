package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.exception.ThreadPostNotFoundException;
import com.lithan.abcjobs.payload.response.ThreadResponse;
import com.lithan.abcjobs.service.ThreadService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ThreadPostController {
    @Autowired
    UserService userService;

    @Autowired
    ThreadService threadService;

    @GetMapping("/u/{username}/thread")
    public ModelAndView threadDetailView(@PathVariable("username") String username, @RequestParam(value = "id", required = false) Long id, Model model) {
        try {
            if (id == null) {
                return new ModelAndView("redirect:/u/" + username + "?tab=threads");
            }
            ThreadResponse threadResponse = threadService.getThreadByUsernameAndThreadId(username, id);

            model.addAttribute("thread", threadResponse.getThreadPost());
            return new ModelAndView("thread/threadDetail");
        } catch (ThreadPostNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // TODO: model.addAttribute("threads", threadService.getAllThreadPosts());
            return new ModelAndView("exception/threadNotFound");
        }
    }

    @GetMapping("/threads")
    public ModelAndView allThreadsView() {
        return new ModelAndView("thread/threads");
    }
}
