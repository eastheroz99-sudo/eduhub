package com.eduhub.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.domain.CourseStatus;
import com.eduhub.course.domain.SessionStatus;
import com.eduhub.course.dto.CourseRequest;
import com.eduhub.course.dto.SessionRequest;
import com.eduhub.course.dto.SessionResponse;
import com.eduhub.course.repository.CourseRepository;
import com.eduhub.course.repository.CourseSessionRepository;
import com.eduhub.global.error.BusinessException;
import com.eduhub.global.error.ErrorCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseAdminServiceTest {

	@Mock
	CourseRepository courseRepository;

	@Mock
	CourseSessionRepository courseSessionRepository;

	@InjectMocks
	CourseAdminService courseAdminService;

	private final LocalDateTime start = LocalDateTime.of(2026, 12, 1, 9, 0);

	@Test
	void 회차_번호는_현재_최대_번호_다음으로_정해진다() {
		Course course = Course.create("자바 기초", "입문", "개발");
		given(courseRepository.findById(1L)).willReturn(Optional.of(course));
		given(courseSessionRepository.findMaxSessionNo(1L)).willReturn(2);
		given(courseSessionRepository.save(any(CourseSession.class))).willAnswer(inv -> inv.getArgument(0));

		SessionResponse result = courseAdminService.addSession(1L,
				new SessionRequest(start, start.plusHours(8), "본사", 30));

		assertThat(result.sessionNo()).isEqualTo(3);
		assertThat(result.remainingSeats()).isEqualTo(30);
		assertThat(result.status()).isEqualTo(SessionStatus.OPEN);
	}

	@Test
	void 과정을_종료하면_회차도_모두_종료된다() {
		Course course = Course.create("자바 기초", "입문", "개발");
		CourseSession s1 = CourseSession.create(course, 1, start, start.plusHours(8), "본사", 30);
		CourseSession s2 = CourseSession.create(course, 2, start.plusDays(7), start.plusDays(7).plusHours(8), "본사", 30);
		given(courseRepository.findById(1L)).willReturn(Optional.of(course));
		given(courseSessionRepository.findByCourseIdOrderBySessionNoAsc(1L)).willReturn(List.of(s1, s2));

		courseAdminService.closeCourse(1L);

		assertThat(course.getStatus()).isEqualTo(CourseStatus.CLOSED);
		assertThat(List.of(s1, s2)).extracting(CourseSession::getStatus).containsOnly(SessionStatus.CLOSED);
	}

	@Test
	void 종료된_과정은_수정하거나_회차를_추가할_수_없다() {
		Course course = Course.create("자바 기초", "입문", "개발");
		course.close();
		given(courseRepository.findById(1L)).willReturn(Optional.of(course));

		assertErrorCode(() -> courseAdminService.updateCourse(1L, new CourseRequest("새 제목", null, "개발")),
				ErrorCode.COURSE_CLOSED);
		assertErrorCode(() -> courseAdminService.addSession(1L, new SessionRequest(start, start.plusHours(8), "본사", 30)),
				ErrorCode.COURSE_CLOSED);
	}

	@Test
	void 종료된_회차는_수정할_수_없다() {
		CourseSession session = CourseSession.create(Course.create("자바 기초", "입문", "개발"),
				1, start, start.plusHours(8), "본사", 30);
		session.close();
		given(courseSessionRepository.findById(1L)).willReturn(Optional.of(session));

		assertErrorCode(() -> courseAdminService.updateSession(1L, new SessionRequest(start, start.plusHours(8), "본사", 40)),
				ErrorCode.SESSION_CLOSED);
	}

	@Test
	void 종료_시각이_시작_시각보다_앞서면_회차를_만들_수_없다() {
		Course course = Course.create("자바 기초", "입문", "개발");
		given(courseRepository.findById(1L)).willReturn(Optional.of(course));

		assertErrorCode(() -> courseAdminService.addSession(1L, new SessionRequest(start, start.minusHours(1), "본사", 30)),
				ErrorCode.INVALID_SESSION_TIME);
	}

	@Test
	void 없는_회차를_수정하면_SESSION_NOT_FOUND_예외가_난다() {
		given(courseSessionRepository.findById(9L)).willReturn(Optional.empty());

		assertErrorCode(() -> courseAdminService.updateSession(9L, new SessionRequest(start, start.plusHours(8), "본사", 30)),
				ErrorCode.SESSION_NOT_FOUND);
	}

	private static void assertErrorCode(ThrowingCallable call, ErrorCode expected) {
		assertThatThrownBy(call)
				.isInstanceOf(BusinessException.class)
				.extracting(e -> ((BusinessException) e).getErrorCode())
				.isEqualTo(expected);
	}
}
