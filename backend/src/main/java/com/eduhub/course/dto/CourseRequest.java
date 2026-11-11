package com.eduhub.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 과정 등록·수정 요청 (FR-03).
 */
public record CourseRequest(
		@NotBlank @Size(max = 100) String title,
		@Size(max = 1000) String description,
		@NotBlank @Size(max = 50) String category) {
}
