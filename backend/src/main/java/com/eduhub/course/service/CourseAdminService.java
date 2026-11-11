package com.eduhub.course.service;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.dto.CourseRequest;
import com.eduhub.course.dto.CourseResponse;
import com.eduhub.course.dto.SessionRequest;
import com.eduhub.course.dto.SessionResponse;
import com.eduhub.course.repository.CourseRepository;
import com.eduhub.course.repository.CourseSessionRepository;
import com.eduhub.global.error.BusinessException;
import com.eduhub.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자의 과정·회차 등록·수정·종료 (FR-03). 삭제는 하지 않고 종료 상태로 바꾼다 (D-03).
 */
@Service
@Transactional(readOnly = true)
public class CourseAdminService {

	private final CourseRepository courseRepository;
	private final CourseSessionRepository courseSessionRepository;

	public CourseAdminService(CourseRepository courseRepository, CourseSessionRepository courseSessionRepository) {
		this.courseRepository = courseRepository;
		this.courseSessionRepository = courseSessionRepository;
	}

	@Transactional
	public CourseResponse createCourse(CourseRequest request) {
		Course course = courseRepository.save(
				Course.create(request.title(), request.description(), request.category()));
		return CourseResponse.from(course);
	}

	@Transactional
	public CourseResponse updateCourse(Long courseId, CourseRequest request) {
		Course course = findCourse(courseId);
		course.update(request.title(), request.description(), request.category());
		return CourseResponse.from(course);
	}

	/** 과정을 종료하면 그 과정의 회차도 모두 종료한다. */
	@Transactional
	public void closeCourse(Long courseId) {
		Course course = findCourse(courseId);
		course.close();
		courseSessionRepository.findByCourseIdOrderBySessionNoAsc(courseId).forEach(CourseSession::close);
	}

	/** 회차 번호는 과정 안의 현재 최대 번호 + 1 */
	@Transactional
	public SessionResponse addSession(Long courseId, SessionRequest request) {
		Course course = findCourse(courseId);
		int nextNo = courseSessionRepository.findMaxSessionNo(courseId) + 1;
		CourseSession session = courseSessionRepository.save(CourseSession.create(
				course, nextNo, request.startsAt(), request.endsAt(), request.location(), request.capacity()));
		return SessionResponse.from(session);
	}

	@Transactional
	public SessionResponse updateSession(Long sessionId, SessionRequest request) {
		CourseSession session = findSession(sessionId);
		session.update(request.startsAt(), request.endsAt(), request.location(), request.capacity());
		return SessionResponse.from(session);
	}

	@Transactional
	public void closeSession(Long sessionId) {
		findSession(sessionId).close();
	}

	private Course findCourse(Long courseId) {
		return courseRepository.findById(courseId)
				.orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
	}

	private CourseSession findSession(Long sessionId) {
		return courseSessionRepository.findById(sessionId)
				.orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
	}
}
