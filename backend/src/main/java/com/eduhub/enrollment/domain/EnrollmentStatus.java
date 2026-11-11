package com.eduhub.enrollment.domain;

/**
 * 수강 신청 상태 (FR-07).
 */
public enum EnrollmentStatus {
	/** 신청 확정 */
	CONFIRMED,
	/** 정원 초과로 대기 (FR-05) */
	WAITING,
	/** 취소 */
	CANCELLED,
	/** 수료 (FR-08) */
	COMPLETED
}
