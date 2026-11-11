package com.eduhub.course.dto;

import java.util.List;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.domain.CourseStatus;

/**
 * 과정 상세와 회차 목록 (FR-02).
 */
public record CourseDetailResponse(
		Long id,
		String title,
		String description,
		String category,
		CourseStatus status,
		List<SessionResponse> sessions) {

	public static CourseDetailResponse of(Course course, List<CourseSession> sessions) {
		return new CourseDetailResponse(
				course.getId(),
				course.getTitle(),
				course.getDescription(),
				course.getCategory(),
				course.getStatus(),
				sessions.stream().map(SessionResponse::from).toList());
	}
}
