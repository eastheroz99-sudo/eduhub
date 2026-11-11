package com.eduhub.course.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 회차 등록·수정 요청 (FR-03). 회차 번호는 서버가 정한다.
 */
public record SessionRequest(
		@NotNull LocalDateTime startsAt,
		@NotNull LocalDateTime endsAt,
		@NotBlank @Size(max = 100) String location,
		@NotNull @Positive Integer capacity) {
}
