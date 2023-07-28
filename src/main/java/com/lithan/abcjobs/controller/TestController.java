package com.lithan.abcjobs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    @GetMapping("/mantap")
    public ModelAndView mantap() {
        return new ModelAndView("mantap");
    }
}
