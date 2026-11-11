package com.eduhub.course.dto;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseStatus;

/**
 * 과정 목록 항목. 엔티티를 직접 노출하지 않는다.
 */
public record CourseResponse(
		Long id,
		String title,
		String description,
		String category,
		CourseStatus status) {

	public static CourseResponse from(Course course) {
		return new CourseResponse(
				course.getId(),
				course.getTitle(),
				course.getDescription(),
				course.getCategory(),
				course.getStatus());
	}
}
