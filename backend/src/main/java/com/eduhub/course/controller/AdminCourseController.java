package com.eduhub.course.controller;

import com.eduhub.course.dto.CourseRequest;
import com.eduhub.course.dto.CourseResponse;
import com.eduhub.course.dto.SessionRequest;
import com.eduhub.course.dto.SessionResponse;
import com.eduhub.course.service.CourseAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 전용. /api/admin/** 은 ADMIN 역할만 접근한다 (SecurityConfig).
 */
@Tag(name = "교육과정 관리", description = "관리자의 과정·회차 등록·수정·종료")
@RestController
@RequestMapping("/api/admin")
public class AdminCourseController {

	private final CourseAdminService courseAdminService;

	public AdminCourseController(CourseAdminService courseAdminService) {
		this.courseAdminService = courseAdminService;
	}

	@Operation(summary = "과정 등록")
	@PostMapping("/courses")
	@ResponseStatus(HttpStatus.CREATED)
	public CourseResponse createCourse(@Valid @RequestBody CourseRequest request) {
		return courseAdminService.createCourse(request);
	}

	@Operation(summary = "과정 수정", description = "종료된 과정은 수정할 수 없다")
	@PutMapping("/courses/{courseId}")
	public CourseResponse updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseRequest request) {
		return courseAdminService.updateCourse(courseId, request);
	}

	@Operation(summary = "과정 종료", description = "과정과 그 과정의 모든 회차를 종료한다")
	@PostMapping("/courses/{courseId}/close")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void closeCourse(@PathVariable Long courseId) {
		courseAdminService.closeCourse(courseId);
	}

	@Operation(summary = "회차 등록", description = "회차 번호는 자동으로 다음 번호가 된다")
	@PostMapping("/courses/{courseId}/sessions")
	@ResponseStatus(HttpStatus.CREATED)
	public SessionResponse addSession(@PathVariable Long courseId, @Valid @RequestBody SessionRequest request) {
		return courseAdminService.addSession(courseId, request);
	}

	@Operation(summary = "회차 수정", description = "정원은 확정 인원보다 적게 줄일 수 없다")
	@PutMapping("/sessions/{sessionId}")
	public SessionResponse updateSession(@PathVariable Long sessionId, @Valid @RequestBody SessionRequest request) {
		return courseAdminService.updateSession(sessionId, request);
	}

	@Operation(summary = "회차 종료")
	@PostMapping("/sessions/{sessionId}/close")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void closeSession(@PathVariable Long sessionId) {
		courseAdminService.closeSession(sessionId);
	}
}
