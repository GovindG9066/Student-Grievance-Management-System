package com.example.demo.services;

import java.util.List;

import com.example.demo.model.Complaint;
import com.example.demo.model.User;

public interface ComplaintService {

    Complaint saveComplaint(Complaint complaint);

    List<Complaint> getComplaintsByUser(User user);

    void deleteComplaint(Long id);

    Complaint getComplaintById(Long id);
    
    Complaint updateComplaint(Long id, Complaint updated);
}