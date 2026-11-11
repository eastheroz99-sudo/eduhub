package com.eduhub.course.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * local 프로필의 초기 데이터(과정 3개: 개발 2, 리더십 1)를 사용한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void 과정_목록은_로그인_없이_페이지_형식으로_조회된다() throws Exception {
		mockMvc.perform(get("/api/courses").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.size").value(2))
				.andExpect(jsonPath("$.totalElements").value(3));
	}

	@Test
	void 분류로_검색할_수_있다() throws Exception {
		mockMvc.perform(get("/api/courses").param("category", "개발"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements").value(2));
	}

	@Test
	void 제목과_분류를_함께_검색할_수_있다() throws Exception {
		mockMvc.perform(get("/api/courses").param("keyword", "스프링").param("category", "개발"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].title").value("스프링 부트 입문"));
	}

	@Test
	void 과정_상세는_회차의_장소와_남은_자리를_포함한다() throws Exception {
		long javaId = findCourseId("자바 기초");

		mockMvc.perform(get("/api/courses/{id}", javaId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.category").value("개발"))
				.andExpect(jsonPath("$.sessions.length()").value(2))
				.andExpect(jsonPath("$.sessions[0].sessionNo").value(1))
				.andExpect(jsonPath("$.sessions[0].location").value("본사 3층 교육장"))
				.andExpect(jsonPath("$.sessions[0].capacity").value(30))
				.andExpect(jsonPath("$.sessions[0].remainingSeats").value(30));
	}

	@Test
	void 없는_과정은_공통_오류_형식의_404를_돌려준다() throws Exception {
		mockMvc.perform(get("/api/courses/99999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("C001"))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void local에서는_Swagger_문서가_열린다() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("EduHub API"));
	}

	private long findCourseId(String title) throws Exception {
		String body = mockMvc.perform(get("/api/courses").param("keyword", title))
				.andReturn().getResponse().getContentAsString();
		return JsonPath.<Number>read(body, "$.content[0].id").longValue();
	}
}
