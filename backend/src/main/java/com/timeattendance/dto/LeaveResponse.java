package com.timeattendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveResponse {

    private Long id;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private String username;
    private LocalDate startDate;
    private LocalDate endDate;

    public LeaveResponse(Long id, String reason, String status, LocalDateTime createdAt, String username , LocalDate startDate , LocalDate endDate) {
        this.id = id;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	// ================= Getters / Setters =================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
