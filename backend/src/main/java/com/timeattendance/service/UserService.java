package com.timeattendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.timeattendance.model.Profile;
import com.timeattendance.model.User;
import com.timeattendance.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register user
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");

        Profile p = user.getProfile();
        if (p != null) {
            p.setUser(user);
            user.setProfile(p);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Update user safely
    public User updateUser(Long id, User userData) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update basic fields
        u.setEmail(userData.getEmail());
        u.setUsername(userData.getUsername());
        u.setRole(userData.getRole());
        u.setPhone(userData.getPhone());

        // Update password if provided
        if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        // Update or create profile safely
        if (userData.getProfile() != null) {
            if (u.getProfile() == null) {
                // No profile yet → create new
                Profile p = new Profile();
                p.setUser(u);
                p.setFullName(userData.getProfile().getFullName());
                p.setPhoneNumber(userData.getProfile().getPhoneNumber());
                p.setProfileImageUrl(userData.getProfile().getProfileImageUrl());
                p.setPosition(userData.getProfile().getPosition());
                u.setProfile(p);
            } else {
                // Profile exists → update fields
                u.getProfile().setFullName(userData.getProfile().getFullName());
                u.getProfile().setPhoneNumber(userData.getProfile().getPhoneNumber());
                u.getProfile().setProfileImageUrl(userData.getProfile().getProfileImageUrl());
                u.getProfile().setPosition(userData.getProfile().getPosition());
            }
        }

        return userRepository.save(u);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
}
