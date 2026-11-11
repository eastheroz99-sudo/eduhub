package com.eduhub.user.dto;

/**
 * 로그인 응답. accessToken을 Authorization: Bearer 헤더에 넣어 보낸다.
 */
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

	public static TokenResponse bearer(String accessToken, long expiresIn) {
		return new TokenResponse(accessToken, "Bearer", expiresIn);
	}
}
