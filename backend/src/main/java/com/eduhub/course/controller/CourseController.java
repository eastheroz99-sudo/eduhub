package com.eduhub.course.controller;

import com.eduhub.course.dto.CourseDetailResponse;
import com.eduhub.course.dto.CourseResponse;
import com.eduhub.course.service.CourseService;
import com.eduhub.global.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "교육과정", description = "교육과정 조회·검색")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@Operation(summary = "교육과정 목록 조회·검색", description = "keyword는 제목 부분 일치, category는 분류 정확히 일치. 둘 다 생략 가능")
	@GetMapping
	public PageResponse<CourseResponse> getCourses(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String category,
			@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return PageResponse.from(courseService.getCourses(keyword, category, pageable));
	}

	@Operation(summary = "교육과정 상세 조회", description = "회차별 일정·장소·정원·남은 자리를 함께 돌려준다")
	@GetMapping("/{id}")
	public CourseDetailResponse getCourse(@PathVariable Long id) {
		return courseService.getCourse(id);
	}
}
