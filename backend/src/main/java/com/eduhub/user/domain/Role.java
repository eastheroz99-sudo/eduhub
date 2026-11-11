package com.eduhub.user.domain;

/**
 * 사용자 역할. 계정당 하나만 가진다 (D-01, D-14).
 */
public enum Role {
	/** 임직원(수강생) */
	EMPLOYEE,
	/** 교육 담당자(관리자) */
	ADMIN
}
