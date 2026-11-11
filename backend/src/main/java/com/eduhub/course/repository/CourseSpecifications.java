package com.eduhub.course.repository;

import java.util.ArrayList;
import java.util.List;

import com.eduhub.course.domain.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * FR-01 검색 조건 (D-18): 제목은 부분 일치, 분류는 정확히 일치. 값이 없으면 조건에서 뺀다.
 */
public final class CourseSpecifications {

	private CourseSpecifications() {
	}

	public static Specification<Course> search(String keyword, String category) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.hasText(keyword)) {
				predicates.add(cb.like(root.get("title"), "%" + keyword.trim() + "%"));
			}
			if (StringUtils.hasText(category)) {
				predicates.add(cb.equal(root.get("category"), category.trim()));
			}
			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
