package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataLoader {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo) {
        return args -> {

            createAdmin(userRepo, "Academic Admin", "academic@admin.com", "ACADEMIC");
            createAdmin(userRepo, "Infrastructure Admin", "infra@admin.com", "INFRASTRUCTURE");
            createAdmin(userRepo, "Hostel Admin", "hostel@admin.com", "HOSTEL");
            createAdmin(userRepo, "Fees Admin", "fees@admin.com", "FEES");
            createAdmin(userRepo, "Technical Admin", "tech@admin.com", "TECHNICAL");
            createAdmin(userRepo, "Ragging Admin", "ragging@admin.com", "RAGGING");
            createAdmin(userRepo, "Other Admin", "other@admin.com", "OTHER");
        };
    }

    private void createAdmin(UserRepository userRepo, String name, String email, String department) {

        // 🔥 GET EXISTING USER
        User admin = userRepo.findByEmail(email);

        if (admin == null) {
            admin = new User();
        }

        admin.setName(name);
        admin.setEmail(email);

        // 🔐 ALWAYS HASH PASSWORD
        admin.setPassword(encoder.encode("Admin@123"));

        admin.setRole("ADMIN");
        admin.setDepartment(department);
        admin.setApproved(true);

        userRepo.save(admin);
    }
}