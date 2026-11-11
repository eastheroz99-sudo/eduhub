package com.eduhub.course.domain;

import java.time.LocalDateTime;

import com.eduhub.global.error.BusinessException;
import com.eduhub.global.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 교육과정의 회차. 수강 신청 단위다 (D-17). 회차 번호는 과정 안에서 유일하다.
 * 확정 인원은 confirmedCount에 두어 남은 자리를 추가 조회 없이 계산한다 (D-21).
 */
@Entity
@Table(name = "course_session",
		uniqueConstraints = @UniqueConstraint(name = "uk_course_session_no", columnNames = {"course_id", "session_no"}))
public class CourseSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(name = "session_no", nullable = false)
	private int sessionNo;

	@Column(nullable = false)
	private LocalDateTime startsAt;

	@Column(nullable = false)
	private LocalDateTime endsAt;

	@Column(nullable = false, length = 100)
	private String location;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false)
	private int confirmedCount;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, length = 20)
	private SessionStatus status;

	protected CourseSession() {
	}

	private CourseSession(Course course, int sessionNo, LocalDateTime startsAt, LocalDateTime endsAt,
			String location, int capacity) {
		validateTime(startsAt, endsAt);
		this.course = course;
		this.sessionNo = sessionNo;
		this.startsAt = startsAt;
		this.endsAt = endsAt;
		this.location = location;
		this.capacity = capacity;
		this.confirmedCount = 0;
		this.status = SessionStatus.OPEN;
	}

	public static CourseSession create(Course course, int sessionNo, LocalDateTime startsAt, LocalDateTime endsAt,
			String location, int capacity) {
		course.checkOpen();
		return new CourseSession(course, sessionNo, startsAt, endsAt, location, capacity);
	}

	public void update(LocalDateTime startsAt, LocalDateTime endsAt, String location, int capacity) {
		checkOpen();
		validateTime(startsAt, endsAt);
		if (capacity < confirmedCount) {
			throw new BusinessException(ErrorCode.CAPACITY_BELOW_CONFIRMED);
		}
		this.startsAt = startsAt;
		this.endsAt = endsAt;
		this.location = location;
		this.capacity = capacity;
	}

	public void close() {
		this.status = SessionStatus.CLOSED;
	}

	public int getRemainingSeats() {
		return capacity - confirmedCount;
	}

	private void checkOpen() {
		if (status == SessionStatus.CLOSED) {
			throw new BusinessException(ErrorCode.SESSION_CLOSED);
		}
	}

	private static void validateTime(LocalDateTime startsAt, LocalDateTime endsAt) {
		if (!endsAt.isAfter(startsAt)) {
			throw new BusinessException(ErrorCode.INVALID_SESSION_TIME);
		}
	}

	public Long getId() {
		return id;
	}

	public Course getCourse() {
		return course;
	}

	public int getSessionNo() {
		return sessionNo;
	}

	public LocalDateTime getStartsAt() {
		return startsAt;
	}

	public LocalDateTime getEndsAt() {
		return endsAt;
	}

	public String getLocation() {
		return location;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getConfirmedCount() {
		return confirmedCount;
	}

	public SessionStatus getStatus() {
		return status;
	}
}
