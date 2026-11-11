package com.eduhub.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.dto.CourseDetailResponse;
import com.eduhub.course.dto.CourseResponse;
import com.eduhub.course.repository.CourseRepository;
import com.eduhub.course.repository.CourseSessionRepository;
import com.eduhub.global.error.BusinessException;
import com.eduhub.global.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

	@Mock
	CourseRepository courseRepository;

	@Mock
	CourseSessionRepository courseSessionRepository;

	@InjectMocks
	CourseService courseService;

	private final Course course = Course.create("자바 기초", "입문", "개발");

	@Test
	@SuppressWarnings("unchecked")
	void 목록은_검색_조건으로_페이지_조회한다() {
		Pageable pageable = PageRequest.of(0, 10);
		given(courseRepository.findAll(any(Specification.class), eq(pageable)))
				.willReturn(new PageImpl<>(List.of(course), pageable, 1));

		Page<CourseResponse> result = courseService.getCourses("자바", "개발", pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).category()).isEqualTo("개발");
	}

	@Test
	void 상세는_회차와_남은_자리를_함께_돌려준다() {
		LocalDateTime start = LocalDateTime.of(2026, 12, 1, 9, 0);
		CourseSession session = CourseSession.create(course, 1, start, start.plusHours(8), "본사 3층", 30);
		given(courseRepository.findById(1L)).willReturn(Optional.of(course));
		given(courseSessionRepository.findByCourseIdOrderBySessionNoAsc(1L)).willReturn(List.of(session));

		CourseDetailResponse result = courseService.getCourse(1L);

		assertThat(result.sessions()).hasSize(1);
		assertThat(result.sessions().get(0).location()).isEqualTo("본사 3층");
		assertThat(result.sessions().get(0).remainingSeats()).isEqualTo(30);
	}

	@Test
	void 없는_과정을_조회하면_COURSE_NOT_FOUND_예외가_난다() {
		given(courseRepository.findById(999L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> courseService.getCourse(999L))
				.isInstanceOf(BusinessException.class)
				.extracting(e -> ((BusinessException) e).getErrorCode())
				.isEqualTo(ErrorCode.COURSE_NOT_FOUND);
	}
}
