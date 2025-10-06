package com.timeattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.timeattendance.service.LeaveService;
import com.timeattendance.service.UserService;
import com.timeattendance.model.LeaveRequest;
import com.timeattendance.model.User;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin
public class LeaveController {
    @Autowired private LeaveService leaveService;
    @Autowired private UserService userService;

    @PostMapping("/request")
    public LeaveRequest requestLeave(@AuthenticationPrincipal UserDetails ud, @RequestBody LeaveRequest req) {
        User user = userService.findByEmail(ud.getUsername()).get();
        req.setUser(user);
        return leaveService.requestLeave(req);
    }

    @GetMapping("/my")
    public List<LeaveRequest> my(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.findByEmail(ud.getUsername()).get();
        return leaveService.getUserLeaves(user);
    }
}
