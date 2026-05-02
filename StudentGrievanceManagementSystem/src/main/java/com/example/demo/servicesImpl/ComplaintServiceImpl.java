package com.example.demo.servicesImpl;

import com.example.demo.model.Complaint;
import com.example.demo.model.User;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.ComplaintService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public Complaint saveComplaint(Complaint complaint) {

        if (complaint.getStatus() == null) {
            complaint.setStatus("Pending");
        }

        if (complaint.getCreatedAt() == null) {
            complaint.setCreatedAt(LocalDateTime.now());
        }

        // TEMP user (later login se aayega)
        if (complaint.getUser() == null) {
            User user = userRepo.findById(1L).orElse(null);
            complaint.setUser(user);
        }

        return complaintRepo.save(complaint);
    }

    @Override
    public List<Complaint> getComplaintsByUser(User user) {
        return complaintRepo.findByUser(user);
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteComplaint(Long id) {
        complaintRepo.deleteById(id);
    }

    @Override
    public Complaint updateComplaint(Long id, Complaint updated) {

        Complaint c = complaintRepo.findById(id).orElse(null);

        if (c != null && "Pending".equalsIgnoreCase(c.getStatus())) {

            c.setTitle(updated.getTitle());
            c.setDescription(updated.getDescription());
            c.setType(updated.getType());
            c.setSubType(updated.getSubType());

            if (updated.getPriority() != null) {
                c.setPriority(updated.getPriority());
            }

            return complaintRepo.save(c);
        }

        return null;
    }
}