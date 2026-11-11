package com.eduhub.course.domain;

/**
 * 교육과정 상태. 과정은 삭제하지 않고 CLOSED(종료)로 바꾼다 (FR-03, D-03).
 */
public enum CourseStatus {
	OPEN,
	CLOSED
}
