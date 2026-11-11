package com.eduhub.global.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정 (D-06). secret은 환경 변수로 주입한다 (local만 기본값). HS256은 32바이트 이상이 필요하다.
 */
@ConfigurationProperties("eduhub.jwt")
public record JwtProperties(String secret, Duration expiration) {

	public JwtProperties {
		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("eduhub.jwt.secret(JWT_SECRET)은 32바이트 이상이어야 합니다.");
		}
		if (expiration == null) {
			expiration = Duration.ofHours(1);
		}
	}
}
