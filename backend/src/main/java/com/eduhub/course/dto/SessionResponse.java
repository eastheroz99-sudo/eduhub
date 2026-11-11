package com.eduhub.course.dto;

import java.time.LocalDateTime;

import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.domain.SessionStatus;

/**
 * 회차 정보와 남은 자리 (FR-02).
 */
public record SessionResponse(
		Long id,
		int sessionNo,
		LocalDateTime startsAt,
		LocalDateTime endsAt,
		String location,
		int capacity,
		int remainingSeats,
		SessionStatus status) {

	public static SessionResponse from(CourseSession session) {
		return new SessionResponse(
				session.getId(),
				session.getSessionNo(),
				session.getStartsAt(),
				session.getEndsAt(),
				session.getLocation(),
				session.getCapacity(),
				session.getRemainingSeats(),
				session.getStatus());
	}
}
