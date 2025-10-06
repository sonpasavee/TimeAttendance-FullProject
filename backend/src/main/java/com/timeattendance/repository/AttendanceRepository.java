package com.timeattendance.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.timeattendance.model.Attendance;
import com.timeattendance.model.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	List<Attendance> findByUserOrderByDateDesc(User user);
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);
}
