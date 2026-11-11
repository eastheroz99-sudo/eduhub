package com.eduhub.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.eduhub.course.domain.Course;
import com.eduhub.course.domain.CourseSession;
import com.eduhub.course.repository.CourseRepository;
import com.eduhub.course.repository.CourseSessionRepository;
import com.eduhub.user.domain.Role;
import com.eduhub.user.domain.User;
import com.eduhub.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * local 프로필에서만 관리자·임직원 계정과 샘플 과정·회차를 만든다 (D-04).
 * 컨트롤러 테스트가 이 데이터(과정 3개: 개발 2, 리더십 1)에 의존한다.
 */
@Component
@Profile("local")
public class LocalDataInitializer implements ApplicationRunner {

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final CourseSessionRepository courseSessionRepository;
	private final PasswordEncoder passwordEncoder;
	private final String adminEmail;
	private final String adminPassword;
	private final String employeeEmail;
	private final String employeePassword;

	public LocalDataInitializer(UserRepository userRepository,
			CourseRepository courseRepository,
			CourseSessionRepository courseSessionRepository,
			PasswordEncoder passwordEncoder,
			@Value("${eduhub.admin.email}") String adminEmail,
			@Value("${eduhub.admin.password}") String adminPassword,
			@Value("${eduhub.employee.email}") String employeeEmail,
			@Value("${eduhub.employee.password}") String employeePassword) {
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.courseSessionRepository = courseSessionRepository;
		this.passwordEncoder = passwordEncoder;
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
		this.employeeEmail = employeeEmail;
		this.employeePassword = employeePassword;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (userRepository.existsByEmail(adminEmail)) {
			return;
		}
		userRepository.save(User.create(adminEmail, passwordEncoder.encode(adminPassword), "교육담당자", Role.ADMIN));
		userRepository.save(User.create(employeeEmail, passwordEncoder.encode(employeePassword), "김사원", Role.EMPLOYEE));

		LocalDate base = LocalDate.now().plusWeeks(2);

		Course java = courseRepository.save(Course.create("자바 기초", "Java 문법과 객체지향 입문", "개발"));
		addSession(java, 1, base, "본사 3층 교육장", 30);
		addSession(java, 2, base.plusWeeks(1), "본사 3층 교육장", 30);

		Course spring = courseRepository.save(Course.create("스프링 부트 입문", "REST API 만들기", "개발"));
		addSession(spring, 1, base.plusDays(2), "온라인", 20);

		Course leadership = courseRepository.save(Course.create("신임 팀장 리더십", "팀 운영과 피드백", "리더십"));
		addSession(leadership, 1, base.plusDays(3), "연수원 A동", 15);
	}

	private void addSession(Course course, int sessionNo, LocalDate date, String location, int capacity) {
		courseSessionRepository.save(CourseSession.create(course, sessionNo,
				LocalDateTime.of(date, LocalTime.of(9, 0)),
				LocalDateTime.of(date, LocalTime.of(18, 0)),
				location, capacity));
	}
}
