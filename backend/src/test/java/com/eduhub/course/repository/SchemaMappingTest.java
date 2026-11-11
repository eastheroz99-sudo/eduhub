package com.eduhub.course.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.domain.SessionStatus;
import com.eduhub.enrollment.domain.Enrollment;
import com.eduhub.enrollment.domain.EnrollmentStatus;
import com.eduhub.enrollment.repository.EnrollmentRepository;
import com.eduhub.user.domain.Role;
import com.eduhub.user.domain.User;
import com.eduhub.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Flyway V1~V3으로 스키마를 만들고 ddl-auto=validate로 엔티티 매핑을 검증한다 (D-20).
 * Docker 없이 H2(MySQL 모드)로 실행한다. 엔티티를 바꿨는데 마이그레이션이 없으면 여기서 깨진다.
 */
@DataJpaTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:jpatest;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=true",
		"spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SchemaMappingTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	CourseSessionRepository courseSessionRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	EntityManager em;

	private final LocalDateTime base = LocalDateTime.of(2026, 12, 1, 9, 0);

	@Test
	void 회차는_번호_순으로_조회되고_최대_번호와_종료_상태가_저장된다() {
		Course course = courseRepository.save(Course.create("자바 기초", "입문", "개발"));
		courseSessionRepository.save(CourseSession.create(course, 2, base.plusWeeks(1), base.plusWeeks(1).plusHours(8), "본사", 30));
		courseSessionRepository.save(CourseSession.create(course, 1, base, base.plusHours(8), "본사", 30));
		em.flush();
		em.clear();

		List<CourseSession> sessions = courseSessionRepository.findByCourseIdOrderBySessionNoAsc(course.getId());
		assertThat(sessions).extracting(CourseSession::getSessionNo).containsExactly(1, 2);
		assertThat(sessions.get(0).getRemainingSeats()).isEqualTo(30);
		assertThat(courseSessionRepository.findMaxSessionNo(course.getId())).isEqualTo(2);

		sessions.get(0).close();
		em.flush();
		em.clear();
		assertThat(courseSessionRepository.findById(sessions.get(0).getId()).orElseThrow().getStatus())
				.isEqualTo(SessionStatus.CLOSED);
	}

	@Test
	void 회차가_없는_과정의_최대_회차_번호는_0이다() {
		Course course = courseRepository.save(Course.create("빈 과정", null, "개발"));

		assertThat(courseSessionRepository.findMaxSessionNo(course.getId())).isZero();
	}

	@Test
	void 같은_임직원은_같은_회차에_두_번_신청할_수_없다() {
		User employee = userRepository.save(User.create("e@eduhub.local", "encoded", "김사원", Role.EMPLOYEE));
		Course course = courseRepository.save(Course.create("자바 기초", "입문", "개발"));
		CourseSession session = courseSessionRepository.save(
				CourseSession.create(course, 1, base, base.plusHours(8), "본사", 30));
		enrollmentRepository.save(Enrollment.create(employee, session, EnrollmentStatus.CONFIRMED));
		em.flush();

		assertThatThrownBy(() -> {
			enrollmentRepository.save(Enrollment.create(employee, session, EnrollmentStatus.WAITING));
			em.flush();
		}).isInstanceOfAny(DataIntegrityViolationException.class, org.hibernate.exception.ConstraintViolationException.class);
	}
}
