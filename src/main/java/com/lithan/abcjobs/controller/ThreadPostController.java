package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.ThreadComment;
import com.lithan.abcjobs.entity.ThreadPost;
import com.lithan.abcjobs.exception.ThreadPostNotFoundException;
import com.lithan.abcjobs.payload.request.ThreadCommentRequest;
import com.lithan.abcjobs.payload.request.ThreadPostRequest;
import com.lithan.abcjobs.payload.response.ThreadResponse;
import com.lithan.abcjobs.service.ThreadPostService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ThreadPostController {
    @Autowired
    UserService userService;

    @Autowired
    ThreadPostService threadPostService;

    @GetMapping("/u/{username}/thread")
    public ModelAndView threadDetailView(@PathVariable("username") String username, @RequestParam(value = "id", required = false) String idStr, Model model) {
        try {
            if (idStr == null || idStr.equals("")) {
                return new ModelAndView("redirect:/u/" + username + "?tab=threads");
            }
            Long id = Long.parseLong(idStr);
            ModelAndView threadDetailPage = new ModelAndView("thread/threadDetail");
            ThreadResponse threadResponse = threadPostService.getThreadByUsernameAndThreadId(username, id);

            ThreadPost currentThread = threadResponse.getThreadPost();

            ThreadCommentRequest threadCommentRequest = new ThreadCommentRequest();
            threadCommentRequest.setThreadPost(currentThread);

            threadDetailPage.addObject("threadCommentRequest", threadCommentRequest);
            currentThread.setFormattedCreatedAt(formatDate(currentThread.getCreatedAt()));

            List<ThreadComment> threadComments = currentThread.getComments();
            threadComments.forEach(threadComment -> threadComment.setFormattedCreatedAt(formatDate(threadComment.getCreatedAt())));

            model.addAttribute("threadComments", threadComments);
            model.addAttribute("thread", currentThread);
            return threadDetailPage;
        } catch (ThreadPostNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView("thread/threads");
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormat.format(date);
    }

    @GetMapping("/threads")
    public ModelAndView viewAllThreadsView(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<ThreadPost> searchedThread = threadPostService.getAllThreadPosts();
        if (searchedThread.size() == 0) {
            model.addAttribute("noResultMessage", "There isn't any thread");
        }
        if (keyword != null) {
            searchedThread = threadPostService.searchForThreadPostsByTitleAndContent(keyword);
        }
        searchedThread.forEach(thread -> thread.setFormattedCreatedAt(formatDate(thread.getCreatedAt())));
        if (searchedThread.size() == 0 && keyword != null) {
            model.addAttribute("noResultMessage", "No search result for: " + keyword);
        }
        model.addAttribute("threadPosts", searchedThread);
        return new ModelAndView("thread/threads");
    }

    @GetMapping("/u/{username}/edit/thread")
    public ModelAndView editTreadDetailView(@RequestParam("id") String threadIdStr, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        Long threadId = Long.parseLong(threadIdStr);
        String threadOwnerUsername = threadPostService.getThreadPostByThreadId(threadId).getUser().getUsername();
        if (principal == null) {
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        }
        String authUsername = principal.getName();
        boolean isAuthUser = authUsername.equals(threadOwnerUsername);
        boolean isAdmin = userService.getUserByUsername(authUsername).getRole().equals("ADMIN");

        if (!(isAuthUser || isAdmin)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You're not allowed to edit this thread!");
            return new ModelAndView("redirect:/u/" + threadOwnerUsername + "/thread?id=" + threadId);
        }

        ThreadPost oldThreadPost = threadPostService.getThreadPostByThreadId(threadId);
        ThreadPostRequest threadPost = new ThreadPostRequest();
        threadPost.setThreadId(oldThreadPost.getThreadId());
        threadPost.setTitle(oldThreadPost.getTitle());
        threadPost.setContent(oldThreadPost.getContent());
        threadPost.setTagName(oldThreadPost.getTag().getTagName());
        model.addAttribute("threadPost", threadPost);
        return new ModelAndView("thread/editThread");
    }
}
