package com.eduhub.enrollment.domain;

import java.time.Instant;

import com.eduhub.course.domain.CourseSession;
import com.eduhub.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 임직원의 회차 수강 신청 (D-17). 임직원·회차 쌍당 한 행이라 같은 회차에 중복 신청할 수 없다 (FR-04).
 * 신청·대기·취소 규칙(FR-04~06)은 3주차에 구현한다.
 */
@Entity
@Table(name = "enrollment",
		uniqueConstraints = @UniqueConstraint(name = "uk_enrollment_employee_session", columnNames = {"employee_id", "session_id"}),
		indexes = @Index(name = "idx_enrollment_session_status", columnList = "session_id, status"))
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private User employee;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", nullable = false)
	private CourseSession session;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, length = 20)
	private EnrollmentStatus status;

	/** 대기 순번은 같은 회차의 WAITING 중 appliedAt 순서다 (FR-05). */
	@Column(nullable = false)
	private Instant appliedAt;

	protected Enrollment() {
	}

	private Enrollment(User employee, CourseSession session, EnrollmentStatus status) {
		this.employee = employee;
		this.session = session;
		this.status = status;
		this.appliedAt = Instant.now();
	}

	public static Enrollment create(User employee, CourseSession session, EnrollmentStatus status) {
		return new Enrollment(employee, session, status);
	}

	public Long getId() {
		return id;
	}

	public User getEmployee() {
		return employee;
	}

	public CourseSession getSession() {
		return session;
	}

	public EnrollmentStatus getStatus() {
		return status;
	}

	public Instant getAppliedAt() {
		return appliedAt;
	}
}
