package com.emotion.ecm.controller;

import com.emotion.ecm.model.AppUser;
import com.emotion.ecm.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private AppUserService userService;

    @Autowired
    public HomeController(AppUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String homePage(Model model) {
        AppUser user = userService.getAuthenticatedUser();
        model.addAttribute("user", user);
        return "home";
    }

}
