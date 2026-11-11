package com.eduhub.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI 문서 정보 (NFR-04). prod에서는 application-prod.yml에서 꺼진다 (D-07).
 * Authorize 버튼에 로그인으로 받은 토큰을 넣으면 관리자 API를 호출할 수 있다 (D-06).
 */
@Configuration
public class OpenApiConfig {

	private static final String BEARER = "bearerAuth";

	@Bean
	public OpenAPI eduhubOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("EduHub API")
						.description("사내 교육과정 조회·수강 신청 시스템")
						.version("v1"))
				.components(new Components().addSecuritySchemes(BEARER, new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(BEARER));
	}
}
