package com.lithan.abcjobs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public ModelAndView mantap(Model model) {
        String text = "Thymeleaf works!";
        model.addAttribute("text", text);
        return new ModelAndView("admin");
    }
}
