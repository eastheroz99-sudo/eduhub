package com.eduhub.course.domain;

import com.eduhub.global.error.BusinessException;
import com.eduhub.global.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 사내 교육과정. 일정·장소·정원은 회차(CourseSession)에 있다.
 */
@Entity
@Table(name = "course", indexes = {
		@Index(name = "idx_course_title", columnList = "title"),
		@Index(name = "idx_course_category", columnList = "category")
})
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(length = 1000)
	private String description;

	/** 분류 (FR-01 검색 조건, D-18) */
	@Column(nullable = false, length = 50)
	private String category;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(nullable = false, length = 20)
	private CourseStatus status;

	protected Course() {
	}

	private Course(String title, String description, String category) {
		this.title = title;
		this.description = description;
		this.category = category;
		this.status = CourseStatus.OPEN;
	}

	public static Course create(String title, String description, String category) {
		return new Course(title, description, category);
	}

	public void update(String title, String description, String category) {
		checkOpen();
		this.title = title;
		this.description = description;
		this.category = category;
	}

	/** FR-03: 종료. 회차 종료는 서비스에서 함께 처리한다. */
	public void close() {
		this.status = CourseStatus.CLOSED;
	}

	public void checkOpen() {
		if (status == CourseStatus.CLOSED) {
			throw new BusinessException(ErrorCode.COURSE_CLOSED);
		}
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getCategory() {
		return category;
	}

	public CourseStatus getStatus() {
		return status;
	}
}
