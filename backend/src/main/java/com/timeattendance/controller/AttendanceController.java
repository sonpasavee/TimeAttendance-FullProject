package com.timeattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.timeattendance.model.Attendance;
import com.timeattendance.model.User;
import com.timeattendance.service.AttendanceService;
import com.timeattendance.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin("http://localhost:3000")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @PostMapping("/clockin")
    public Attendance clockIn(@AuthenticationPrincipal UserDetails ud, @RequestBody(required=false) Map<String,String> body) {
        User user = userService.findByEmail(ud.getUsername()).get();
        String method = body != null ? body.getOrDefault("method","MANUAL") : "MANUAL";
        String location = body != null ? body.getOrDefault("location",null) : null;
        return attendanceService.clockIn(user, method, location);
    }

    @PostMapping("/clockout")
    public ResponseEntity<?> clockOut(@AuthenticationPrincipal UserDetails ud) {
        try {
            User user = userService.findByEmail(ud.getUsername()).get();
            Attendance attendance = attendanceService.clockOut(user);
            return ResponseEntity.ok(attendance); // return entity แทน int
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/my")
    public List<Attendance> getMyAttendance(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.findByEmail(ud.getUsername()).get();
        return attendanceService.getUserAttendance(user);
    }
}
