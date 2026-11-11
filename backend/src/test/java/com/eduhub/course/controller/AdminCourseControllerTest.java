package com.eduhub.course.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * FR-03 관리자 API. local 초기 계정으로 실제 로그인해 받은 토큰을 쓴다.
 * 각 테스트는 롤백되어 다른 테스트의 데이터에 영향을 주지 않는다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminCourseControllerTest {

	private static final String COURSE_JSON = """
			{"title": "테스트 자동화", "description": "JUnit 실습", "category": "개발"}
			""";
	private static final String SESSION_JSON = """
			{"startsAt": "2026-12-10T09:00:00", "endsAt": "2026-12-10T18:00:00", "location": "본사 5층", "capacity": 2}
			""";

	@Autowired
	MockMvc mockMvc;

	@Test
	void 토큰_없이_관리자_API를_부르면_401() throws Exception {
		mockMvc.perform(post("/api/admin/courses").contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("G002"));
	}

	@Test
	void 잘못된_토큰이면_401() throws Exception {
		mockMvc.perform(post("/api/admin/courses")
						.header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt")
						.contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("G002"));
	}

	@Test
	void 임직원_토큰으로_관리자_API를_부르면_403() throws Exception {
		String token = login("employee@eduhub.local", "local-employee-only");

		mockMvc.perform(post("/api/admin/courses")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("G003"));
	}

	@Test
	void 비밀번호가_틀리면_로그인_실패_401() throws Exception {
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"admin@eduhub.local\", \"password\": \"wrong\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("A001"));
	}

	@Test
	void 관리자는_과정과_회차를_등록하고_조회에_반영된다() throws Exception {
		String token = adminToken();
		long courseId = createCourse(token);

		mockMvc.perform(post("/api/admin/courses/{id}/sessions", courseId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(SESSION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.sessionNo").value(1))
				.andExpect(jsonPath("$.remainingSeats").value(2));

		mockMvc.perform(get("/api/courses/{id}", courseId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("테스트 자동화"))
				.andExpect(jsonPath("$.sessions[0].location").value("본사 5층"));
	}

	@Test
	void 관리자는_과정을_수정하고_종료하며_종료된_과정은_수정할_수_없다() throws Exception {
		String token = adminToken();
		long courseId = createCourse(token);

		mockMvc.perform(put("/api/admin/courses/{id}", courseId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\": \"테스트 자동화 심화\", \"category\": \"개발\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("테스트 자동화 심화"));

		mockMvc.perform(post("/api/admin/courses/{id}/close", courseId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isNoContent());

		mockMvc.perform(put("/api/admin/courses/{id}", courseId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("C003"));
	}

	@Test
	void 필수값이_없으면_400() throws Exception {
		mockMvc.perform(post("/api/admin/courses")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
						.contentType(MediaType.APPLICATION_JSON).content("{\"title\": \"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("G001"));
	}

	private String adminToken() throws Exception {
		return login("admin@eduhub.local", "local-admin-only");
	}

	private String login(String email, String password) throws Exception {
		String body = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, password)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$.accessToken");
	}

	private long createCourse(String token) throws Exception {
		String body = mockMvc.perform(post("/api/admin/courses")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("OPEN"))
				.andReturn().getResponse().getContentAsString();
		return JsonPath.<Number>read(body, "$.id").longValue();
	}
}
