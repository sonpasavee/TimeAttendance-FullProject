package com.timeattendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.timeattendance.model.Attendance;
import com.timeattendance.model.User;
import com.timeattendance.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // Clock In
    public Attendance clockIn(User user, String method, String location) {
        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository.findByUserAndDate(user, today).orElse(null);

        if (att == null) {
            // ยังไม่มี record ของวันนี้ → สร้างใหม่
            att = new Attendance();
            att.setUser(user);
            att.setDate(today);
            att.setClockIn(LocalDateTime.now());
            att.setMethod(method);
            att.setLocation(location);

            int hour = LocalDateTime.now().getHour();
            att.setStatus(hour < 9 ? "ON_TIME" : "LATE");

        } else {
            // มี record อยู่แล้ว → ตรวจสอบว่า clock in แล้วหรือยัง
            if (att.getClockIn() != null) {
                throw new RuntimeException("You have already clocked in today");
            } else {
                att.setClockIn(LocalDateTime.now());
                att.setMethod(method);
                att.setLocation(location);
                int hour = LocalDateTime.now().getHour();
                att.setStatus(hour < 8 ? "ON_TIME" : "LATE");
            }
        }

        return attendanceRepository.save(att);
    }

    // Clock Out
    public Attendance clockOut(User user) {
        LocalDate today = LocalDate.now();
        Attendance att = attendanceRepository.findByUserAndDate(user, today).orElse(null);

        if (att == null) {
            // ยังไม่มี record ของวันนี้ → สร้างใหม่
            att = new Attendance();
            att.setUser(user);
            att.setDate(today);
            att.setClockOut(LocalDateTime.now());
        } else {
            if (att.getClockOut() != null) {
                throw new RuntimeException("You have already clocked out today");
            }
            att.setClockOut(LocalDateTime.now());
        }

        return attendanceRepository.save(att);
    }

    // ดึงประวัติการเข้าออกของผู้ใช้
    public List<Attendance> getUserAttendance(User user) {
        return attendanceRepository.findByUserOrderByDateDesc(user);
    }

    // ดึงประวัติระหว่างวันที่
    public List<Attendance> findBetween(LocalDate start, LocalDate end) {
        return attendanceRepository.findByDateBetween(start, end);
    }
    
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

}
