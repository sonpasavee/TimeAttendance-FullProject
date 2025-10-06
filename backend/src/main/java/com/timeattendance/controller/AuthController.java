package com.timeattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.timeattendance.model.User;
import com.timeattendance.service.UserService;
import com.timeattendance.security.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User saved = userService.register(user);

            // ใช้ HashMap แทน Map.of เพื่อหลีกเลี่ยง null
            Map<String, Object> response = new HashMap<>();
            response.put("email", saved.getEmail() != null ? saved.getEmail() : "");
            response.put("role", saved.getRole() != null ? saved.getRole() : "USER");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to register user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // Login ด้วย email
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("Login email: " + user.getEmail());
        System.out.println("Login password: " + user.getPassword());

        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            System.out.println("Authenticated: " + auth.isAuthenticated());

            Optional<User> optionalUser = userService.findByEmail(user.getEmail());
            if (optionalUser.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            User u = optionalUser.get();
            String token = tokenProvider.generateToken(
                u.getEmail() != null ? u.getEmail() : "",
                u.getRole() != null ? u.getRole() : "USER"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("email", u.getEmail() != null ? u.getEmail() : "");
            response.put("role", u.getRole() != null ? u.getRole() : "USER");
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
