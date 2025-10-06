package com.timeattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.timeattendance.dto.AttendanceResponse;
import com.timeattendance.dto.LeaveResponse;
import com.timeattendance.model.LeaveRequest;
import com.timeattendance.model.User;
import com.timeattendance.repository.LeaveRequestRepository;
import com.timeattendance.security.JwtTokenProvider;
import com.timeattendance.service.AttendanceService;
import com.timeattendance.service.LeaveService;
import com.timeattendance.service.UserService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("http://localhost:3000")
public class AdminController {

    @Autowired 
    private UserService userService;
    @Autowired 
    private LeaveService leaveService;
    @Autowired 
    private AttendanceService attendanceService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    // ดึง user ทั้งหมด
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // สร้าง user ใหม่
    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User saved = userService.register(user);
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "email", saved.getEmail(),
                "username", saved.getUsername()
        ));
    }
    
    @GetMapping("/attendance/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<AttendanceResponse> getAllAttendance() {
        return attendanceService.findAll().stream()
            .map(a -> new AttendanceResponse(
                a.getId(),
                a.getUser() != null ? a.getUser().getUsername() : null,
                a.getDate(),
                a.getClockIn(),
                a.getClockOut(),
                a.getStatus(),
                a.getMethod(),
                a.getLocation()
            )).collect(Collectors.toList());
    }


    // update user (Admin) – เปลี่ยนเฉพาะ username
    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // อัปเดตเฉพาะ username
        if (payload.containsKey("username")) {
            existingUser.setUsername(payload.get("username"));
        }

        User updated = userService.save(existingUser); // save โดยไม่แก้ password/email
        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "email", updated.getEmail(),
                "username", updated.getUsername()
        ));
    }

    // ลบ user
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // Pending leave requests
 // Pending leave requests
    @GetMapping("/leave/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<LeaveResponse> getPendingLeaves() {
        List<LeaveRequest> leaves = leaveRequestRepository.findByStatus("PENDING");

        // map entity -> DTO (เพิ่ม startDate, endDate)
        return leaves.stream()
            .map(lr -> new LeaveResponse(
                    lr.getId(),
                    lr.getReason(),
                    lr.getStatus(),
                    lr.getCreatedAt(),
                    lr.getUser() != null ? lr.getUser().getUsername() : null,
                    lr.getStartDate(),  // ✅ เพิ่ม
                    lr.getEndDate()     // ✅ เพิ่ม
            ))
            .collect(Collectors.toList());
    }

    

    // Approve leave
    @PutMapping("/leave/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public LeaveRequest approve(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getUsername(token);
        User admin = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return leaveService.decide(id, "APPROVED", admin.getId());
    }

    // Reject leave
    @PutMapping("/leave/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public LeaveRequest reject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getUsername(token);
        User admin = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return leaveService.decide(id, "REJECTED", admin.getId());
    }
}
