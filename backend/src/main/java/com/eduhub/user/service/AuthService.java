package com.eduhub.user.service;

import com.eduhub.global.error.BusinessException;
import com.eduhub.global.error.ErrorCode;
import com.eduhub.global.security.JwtTokenProvider;
import com.eduhub.user.domain.User;
import com.eduhub.user.dto.LoginRequest;
import com.eduhub.user.dto.TokenResponse;
import com.eduhub.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인과 토큰 발급 (D-06). 회원가입은 없다: 계정은 관리자가 준비한다.
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/** 이메일이 없거나, 비밀번호가 틀리거나, 비활성 계정이면 같은 오류(LOGIN_FAILED)를 낸다. */
	public TokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.filter(User::isActive)
				.filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
				.orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));
		return TokenResponse.bearer(jwtTokenProvider.issue(user), jwtTokenProvider.expiresInSeconds());
	}
}
