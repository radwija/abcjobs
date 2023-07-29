package com.lithan.abcjobs.controller;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;
    @GetMapping({"","/","/dashboard"})
    public ModelAndView mantap(Model model) {
        List<User> users = userService.getAllUsers();
        String text = "Thymeleaf works!";
        model.addAttribute("text", text);
        model.addAttribute("users", users);
        return new ModelAndView("admin/admin");
    }
}
