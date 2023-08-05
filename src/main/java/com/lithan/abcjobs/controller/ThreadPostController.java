package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.exception.ThreadPostNotFoundException;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;
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
            ModelAndView threadDetailPage = new ModelAndView("thread/threadDetail");
            if (id == null) {
                return new ModelAndView("redirect:/u/" + username + "?tab=threads");
            }
            ThreadResponse threadResponse = threadService.getThreadByUsernameAndThreadId(username, id);

            ThreadPost currentThread = threadResponse.getThreadPost();

            ThreadCommentRequest threadCommentRequest = new ThreadCommentRequest();
            threadCommentRequest.setThreadPost(currentThread);

            threadDetailPage.addObject("threadCommentRequest", threadCommentRequest);
            model.addAttribute("thread", currentThread);
            return threadDetailPage;
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
