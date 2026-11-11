package com.eduhub.global.error;

import java.time.Instant;

/**
 * 모든 오류 응답의 공통 형식: {code, message, timestamp}.
 */
public record ErrorResponse(String code, String message, Instant timestamp) {

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), Instant.now());
	}

	public static ErrorResponse of(ErrorCode errorCode, String message) {
		return new ErrorResponse(errorCode.getCode(), message, Instant.now());
	}

	/**
	 * 컨트롤러 밖(보안 필터)에서 응답을 쓸 때 사용한다. 메시지는 ErrorCode의 고정 문구만 들어온다.
	 */
	public String toJson() {
		return "{\"code\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}".formatted(code, message, timestamp);
	}
}
