package com.eduhub.global.error;

import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체에서 쓰는 오류 코드. 응답 형식은 ErrorResponse.
 */
public enum ErrorCode {

	// 공통
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "G001", "입력값이 올바르지 않습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "G002", "로그인이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "G003", "접근 권한이 없습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "G004", "요청한 경로를 찾을 수 없습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "서버 오류가 발생했습니다."),

	// 인증
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A001", "이메일 또는 비밀번호가 올바르지 않습니다."),

	// 교육과정·회차
	COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "교육과정을 찾을 수 없습니다."),
	SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "회차를 찾을 수 없습니다."),
	COURSE_CLOSED(HttpStatus.CONFLICT, "C003", "종료된 교육과정은 변경할 수 없습니다."),
	SESSION_CLOSED(HttpStatus.CONFLICT, "C004", "종료된 회차는 변경할 수 없습니다."),
	CAPACITY_BELOW_CONFIRMED(HttpStatus.CONFLICT, "C005", "정원은 이미 확정된 인원보다 적을 수 없습니다."),
	INVALID_SESSION_TIME(HttpStatus.BAD_REQUEST, "C006", "회차 종료 시각은 시작 시각보다 뒤여야 합니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
