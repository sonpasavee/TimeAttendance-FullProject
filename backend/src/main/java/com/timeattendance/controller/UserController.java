package com.timeattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.timeattendance.service.UserService;
import com.timeattendance.model.User;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:3000")
public class UserController {
	@Autowired private UserService userService;
	
	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails ud) {
		User u = userService.findByEmail(ud.getUsername()).get();
		u.setPassword(null);
		return ResponseEntity.ok(u); 
	}
	
	@PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails ud, @RequestBody User payload) {
        User u = userService.findByEmail(ud.getUsername()).get();
        // reuse updateUser by id
        payload.setPassword(payload.getPassword());
        User updated = userService.updateUser(u.getId(), payload);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }

}
