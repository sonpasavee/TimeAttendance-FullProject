package com.timeattendance.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.timeattendance.model.LeaveRequest;
import com.timeattendance.model.User;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserOrderByCreatedAtDesc(User user);
    @EntityGraph(attributePaths = {"user"})
    List<LeaveRequest> findByStatus(String status);
}
