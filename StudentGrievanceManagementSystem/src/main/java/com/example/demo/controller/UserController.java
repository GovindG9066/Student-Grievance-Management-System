package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ================= REGISTER =================
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           Model model) {

        // 🔥 FIELD VALIDATION (password etc.)
        if (result.hasErrors()) {
            return "StudentRegistration";
        }

        // 🔥 EMAIL CHECK
        if (userRepo.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered!");
            return "StudentRegistration";
        }

        user.setRole("STUDENT");
        user.setApproved(true);
        user.setDepartment(null);

        // 🔐 PASSWORD HASH
        user.setPassword(encoder.encode(user.getPassword()));

        userRepo.save(user);

        return "redirect:/login";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@ModelAttribute User user,
                        HttpSession session,
                        Model model) {

        User existing = userRepo.findByEmail(user.getEmail());

        if (existing == null) {
            model.addAttribute("error", "User not found");
            return "StudentLogin";
        }

        if (!encoder.matches(user.getPassword(), existing.getPassword())) {
            model.addAttribute("error", "Invalid password");
            return "StudentLogin";
        }

        session.setAttribute("user", existing);

        if ("ADMIN".equals(existing.getRole())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }

    // ================= PROFILE =================
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        return "profile";
    }

    // ================= UPDATE PROFILE =================
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User updatedUser,
                                HttpSession session,
                                Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        user.setName(updatedUser.getName());

        // 🔐 PASSWORD UPDATE
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {

            if (!updatedUser.getPassword().matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
                model.addAttribute("error", "Weak password!");
                return "profile";
            }

            user.setPassword(encoder.encode(updatedUser.getPassword()));
        }

        userRepo.save(user);
        session.setAttribute("user", user);

        return "redirect:/student/dashboard";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}