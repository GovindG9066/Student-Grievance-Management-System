package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type;
    private String subType;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String department; // 🔥 IMPORTANT (ADMIN FILTERING)

    @Column(nullable = false)
    private String status;   // Pending / In Progress / Resolved

    private String priority; // Low / Medium / High

    private LocalDateTime createdAt;
    
    @Column(length = 500)
    private String rejectReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 🔥 Auto set values before saving
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
        if (priority == null) {
            priority = "MEDIUM";
        }
    }

    // Constructors
    public Complaint() {}

    public Complaint(String title, String type, String subType,
                     String description, String department,
                     String status, String priority,
                     LocalDateTime createdAt, User user) {
        this.title = title;
        this.type = type;
        this.subType = subType;
        this.description = description;
        this.department = department;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.user = user;
    }

    // Getters & Setters

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}