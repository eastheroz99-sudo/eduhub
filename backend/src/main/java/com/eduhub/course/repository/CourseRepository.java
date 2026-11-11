package com.eduhub.course.repository;

import com.eduhub.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 과정 목록은 연관관계가 없는 단일 테이블 조회라 N+1이 생기지 않는다 (NFR-02).
 * 검색 조건은 CourseSpecifications로 조합한다.
 */
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
}
