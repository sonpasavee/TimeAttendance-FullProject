package com.timeattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.timeattendance.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // หา user ตาม email
}
