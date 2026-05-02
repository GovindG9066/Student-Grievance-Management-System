package com.example.demo.controller;

import com.example.demo.model.Complaint;
import com.example.demo.model.User;
import com.example.demo.repository.ComplaintRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepo;

    // 🔥 SUB TYPES
    private List<String> getSubTypes(String type) {

        if ("academic".equals(type)) {
            return List.of("Exam Issue", "Result Error", "Attendance Issue",
                    "Internal Marks Issue", "Faculty Behavior", "Teaching Issue");
        } 
        else if ("infrastructure".equals(type)) {
            return List.of("Classroom Maintenance", "Lab Equipment Issue", "Library Issue",
                    "WiFi Problem", "Cleanliness Issue", "Electrical Problem");
        } 
        else if ("hostel".equals(type)) {
            return List.of("Room Maintenance", "Water Problem", "Electricity Issue",
                    "Mess Food Complaint", "Security Issue");
        } 
        else if ("fees".equals(type)) {
            return List.of("Fees Payment Issue", "Scholarship Issue",
                    "Document Verification Delay", "Bonafide / TC Issue", "ID Card Issue");
        } 
        else if ("technical".equals(type)) {
            return List.of("Portal Login Issue", "Website Error",
                    "WiFi Not Working", "Computer Lab Issue");
        } 
        else if ("ragging".equals(type)) {
            return List.of("Ragging", "Misbehavior", "Bullying", "Discrimination");
        } 
        else {
            return List.of("Other Issue");
        }
    }

    // 🔥 SHOW FORM
    @GetMapping("/complaints")
    public String complaintsPage(@RequestParam(required = false) String type,
                                 @RequestParam(required = false) Long editId,
                                 Model model,
                                 HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Complaint complaint = null;

        if (editId != null) {
            complaint = complaintRepo.findById(editId).orElse(null);
            if (complaint != null) {
                type = complaint.getType();
            }
        }

        List<String> subTypes = getSubTypes(type);

        model.addAttribute("selectedType", type);
        model.addAttribute("subTypes", subTypes);
        model.addAttribute("complaint", complaint);

        return "complaints";
    }

    // 📝 ADD
    @PostMapping("/complaints")
    public String addComplaint(Complaint complaint, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        complaint.setUser(user);
        complaint.setStatus("PENDING");
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setDepartment(complaint.getType().toUpperCase());

        complaintRepo.save(complaint);

        return "redirect:/student/dashboard";
    }

    // 📊 STUDENT DASHBOARD
    @GetMapping("/student/dashboard")
    public String dashboard(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("complaints", complaintRepo.findByUser(user));
        model.addAttribute("user", user);

        return "dashboard";
    }

    // ❌ DELETE
    @GetMapping("/delete/{id}")
    public String deleteComplaint(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("user");
        Complaint complaint = complaintRepo.findById(id).orElse(null);

        if (user != null && complaint != null &&
            complaint.getUser() != null &&
            complaint.getUser().getId().equals(user.getId())) {

            complaintRepo.deleteById(id);
        }

        return "redirect:/student/dashboard";
    }

    // ✏️ EDIT
    @GetMapping("/edit/{id}")
    public String editComplaint(@PathVariable Long id) {
        return "redirect:/complaints?editId=" + id;
    }

    // ✏️ UPDATE
    @PostMapping("/update/{id}")
    public String updateComplaint(@PathVariable Long id,
                                 Complaint updated,
                                 HttpSession session) {

        User user = (User) session.getAttribute("user");
        Complaint c = complaintRepo.findById(id).orElse(null);

        if (c != null && user != null &&
            c.getUser() != null &&
            c.getUser().getId().equals(user.getId()) &&
            "PENDING".equalsIgnoreCase(c.getStatus())) {

            c.setTitle(updated.getTitle());
            c.setDescription(updated.getDescription());
            c.setType(updated.getType());
            c.setSubType(updated.getSubType());

            complaintRepo.save(c);
        }

        return "redirect:/student/dashboard";
    }

    // 🔥 ADMIN DASHBOARD
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        List<Complaint> complaints =
                complaintRepo.findByDepartment(admin.getDepartment());

        model.addAttribute("complaints", complaints);

        return "admin-dashboard";
    }

    // 🔥 APPROVE / PROGRESS
    @GetMapping("/admin/update-status")
    public String updateStatus(@RequestParam Long id,
                               @RequestParam String status,
                               HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        Complaint complaint = complaintRepo.findById(id).orElse(null);

        if (complaint != null) {

            // 🔒 LOCK FINAL
            if ("APPROVED".equalsIgnoreCase(complaint.getStatus()) ||
                "REJECTED".equalsIgnoreCase(complaint.getStatus())) {

                return "redirect:/admin/dashboard";
            }

            complaint.setStatus(status.toUpperCase());
            complaintRepo.save(complaint);
        }

        return "redirect:/admin/dashboard";
    }

    // 🔥 REJECT WITH REASON (MAIN FEATURE)
    @GetMapping("/admin/reject")
    public String rejectComplaint(@RequestParam Long id,
                                 @RequestParam String reason,
                                 HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.getRole().equals("ADMIN")) {
            return "redirect:/login";
        }

        Complaint complaint = complaintRepo.findById(id).orElse(null);

        if (complaint != null) {

            if ("APPROVED".equalsIgnoreCase(complaint.getStatus()) ||
                "REJECTED".equalsIgnoreCase(complaint.getStatus())) {

                return "redirect:/admin/dashboard";
            }

            complaint.setStatus("REJECTED");

            // 🔥 SAVE REASON
            complaint.setRejectReason(reason);

            complaintRepo.save(complaint);
        }

        return "redirect:/admin/dashboard";
    }
}