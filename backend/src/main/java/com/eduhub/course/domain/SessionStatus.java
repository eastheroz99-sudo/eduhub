package com.eduhub.course.domain;

/**
 * 회차 상태. 종료된 회차는 수정·신청할 수 없다 (FR-03).
 */
public enum SessionStatus {
	OPEN,
	CLOSED
}
