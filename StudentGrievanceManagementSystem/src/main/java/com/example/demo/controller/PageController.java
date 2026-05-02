package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.example.demo.model.User;

@Controller
public class PageController {

    // ================= HOME =================
    @GetMapping("/")
    public String home() {
        return "redirect:/login"; // 🔥 better than direct return
    }

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage() {
        return "StudentLogin";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User()); // 🔥 REQUIRED for Thymeleaf binding
        return "StudentRegistration";
    }

    // ================= ADMIN DASHBOARD =================
    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}