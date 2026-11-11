package com.eduhub.global.security;

import java.time.Instant;

import com.eduhub.user.domain.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * 액세스 토큰 발급. subject는 사용자 id, role 클레임은 역할 이름이다.
 * 검증은 SecurityConfig의 JwtDecoder(리소스 서버)가 한다.
 */
@Component
public class JwtTokenProvider {

	public static final String ROLE_CLAIM = "role";

	private final JwtEncoder jwtEncoder;
	private final JwtProperties properties;

	public JwtTokenProvider(JwtEncoder jwtEncoder, JwtProperties properties) {
		this.jwtEncoder = jwtEncoder;
		this.properties = properties;
	}

	public String issue(User user) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("eduhub")
				.subject(String.valueOf(user.getId()))
				.issuedAt(now)
				.expiresAt(now.plus(properties.expiration()))
				.claim(ROLE_CLAIM, user.getRole().name())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long expiresInSeconds() {
		return properties.expiration().toSeconds();
	}
}
