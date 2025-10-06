package com.timeattendance.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.timeattendance.repository.LeaveRequestRepository;
import com.timeattendance.model.LeaveRequest;
import com.timeattendance.model.User;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class LeaveService {
	
	@Autowired private LeaveRequestRepository leaveRepo;
	
	public LeaveRequest requestLeave(LeaveRequest req) {
		req.setStatus("PENDING");
		req.setCreatedAt(LocalDateTime.now());
		return leaveRepo.save(req);
	}
	
	public List<LeaveRequest> getUserLeaves(User user) {
        return leaveRepo.findByUserOrderByCreatedAtDesc(user);
    }

	public List<LeaveRequest> getPending() {
        return leaveRepo.findByStatus("PENDING");
    }
	
	public LeaveRequest decide(Long id , String status , Long adminId) {
		LeaveRequest r = leaveRepo.findById(id).orElse(null);
		r.setStatus(status);
		r.setDecidedAt(LocalDateTime.now());
		r.setDecidedById(adminId);
		return leaveRepo.save(r);
	}

}
