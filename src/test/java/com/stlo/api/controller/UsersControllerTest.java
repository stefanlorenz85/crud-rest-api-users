package com.stlo.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stlo.api.model.Login;
import com.stlo.api.model.db.User;
import com.stlo.api.service.UsersService;

@ActiveProfiles("test")
@WebMvcTest(controllers = UsersController.class)
class UsersControllerTest {

	@MockBean
	private UsersService usersService;

	@Autowired
	private UsersController usersController;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;
	
	@BeforeEach
	void init() {
		// https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-mockmvc.html#test-mockmvc-setup
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
	}

	@Test
	@DisplayName("#createUser with empty body should return 404 and not call service")
	void testCreateUser_withEmptyBody() throws Exception {

		mvc.perform(post("/api/rest/users").with(csrf()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(this.usersService, times(0)).createUser(any(User.class));
	}

	@Test
	@DisplayName("#createUser with missing property in body should return 404 and not call service")
	void testCreateUser_withMissingProperty() throws Exception {

		mvc.perform(post("/api/rest/users").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(new User())))
				.andDo(print())
				.andExpect(status().isBadRequest());

		verify(this.usersService, times(0)).createUser(any(User.class));
	}

	@Test
	@DisplayName("#createUser with valid body should call service and return 200")
	void testCreateUser_withValidBody() throws Exception {
		var captor = ArgumentCaptor.forClass(User.class);
		var user = new User();
		user.setName("MyName");
		when(this.usersService.createUser(any(User.class))).thenReturn(user);

		mvc.perform(post("/api/rest/users").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(user)))
				.andDo(print())
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", is("MyName")));

		verify(this.usersService, times(1)).createUser(captor.capture());
		assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(user);
	}

	@Test
	@DisplayName("#getUser with id exists should call service and return 200 ")
	void testGetUser() throws Exception {
		var user = new User();
		user.setName("MyName");
		when(this.usersService.getUser(1L)).thenReturn(Optional.of(user));

		mvc.perform(get("/api/rest/users/1").with(csrf()))
				.andDo(print())
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", is("MyName")));

		verify(this.usersService, times(1)).getUser(1L);
	}

	@Test
	@DisplayName("#getUser with invalid id return 400 ")
	void testGetUser_invalidId() throws Exception {

		mvc.perform(get("/api/rest/users/abc").with(csrf()))
				.andDo(print())
				.andExpect(status().isBadRequest());

		verify(this.usersService, times(0)).getUser(any());
	}

	@Test
	@DisplayName("#getUser with id not exists should call service and return 404")
	void testGetUser_notExistingId() throws Exception {
		when(this.usersService.getUser(1L)).thenReturn(Optional.empty());

		mvc.perform(get("/api/rest/users/1").with(csrf()))
				.andDo(print())
				.andExpect(status().isNotFound());

		verify(this.usersService, times(1)).getUser(1L);
	}

	@Test
	@DisplayName("#getUserPage without pageable should take default value and return page with 200")
	void testGetUserPage_withoutPageable() throws Exception {
		var user = new User();
		user.setName("MyName");
		var page = new PageImpl<User>(List.of(user));
		when(this.usersService.getUserPage(any())).thenReturn(page);

		mvc.perform(get("/api/rest/users").with(csrf()))
				.andDo(print())
				.andExpectAll(
						status().isOk(),
						jsonPath("$.content[0].name", is("MyName")),
						jsonPath("$.totalElements", is(1)));

		verify(this.usersService, times(1)).getUserPage(PageRequest.of(0, 20));
	}

	@Test
	@DisplayName("#getUserPage with pageable return page with 200")
	void testGetUserPage_withPageable() throws Exception {
		var page = new PageImpl<User>(List.of(new User()));
		when(this.usersService.getUserPage(any())).thenReturn(page);

		mvc.perform(get("/api/rest/users?size=123&page=2").with(csrf()))
				.andDo(print())
				.andExpect(status().isOk());

		verify(this.usersService, times(1)).getUserPage(PageRequest.of(2, 123));
	}

	@Test
	@DisplayName("#removeUser with id not exists should call service and return 404")
	void testRemoveUser() throws Exception {

		mvc.perform(delete("/api/rest/users/1").with(csrf()))
				.andDo(print())
				.andExpect(status().isNoContent());

		verify(this.usersService, times(1)).removeUser(1L);
	}

	@Test
	@DisplayName("#removeUser with invalid id return 400 ")
	void testRemoveUser_invalidId() throws Exception {

		mvc.perform(delete("/api/rest/users/abc").with(csrf()))
				.andDo(print())
				.andExpect(status().isBadRequest());

		verify(this.usersService, times(0)).removeUser(any());
	}

	@Test
	@DisplayName("#login with unmatched password should return 401")
	void testLogin_unmatchedPassword() throws Exception {
		when(this.usersService.login(any())).thenReturn(false);
		var login = new Login();
		login.setPassword("lorem ipsum doret");
		login.setUserId(1L);

		mvc.perform(post("/api/rest/users/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(login)))
				.andDo(print()) 
				.andExpect(status().isUnauthorized()); 

		verify(this.usersService, times(0)).getUser(any());
	}

	@Test
	@DisplayName("#login with password null short should return 400")
	void testLogin_nullPassword() throws Exception {
		var login = new Login();
		login.setPassword(null);
		login.setUserId(1L); 
		
		mvc.perform(post("/api/rest/users/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(login)))
				.andDo(print()) 
				.andExpect(status().isBadRequest()); 

		verify(this.usersService, times(0)).login(any());
	}

	@Test
	@DisplayName("#login with matched password and user should return 200")
	void testLogin_matchedPasswordAndUser() throws Exception {
		when(this.usersService.login(any())).thenReturn(true);
		var user = new User();
		user.setName("Jon");
		when(this.usersService.getUser(any())).thenReturn(Optional.of(user));
		var login = new Login();
		login.setPassword("lorem ipsum doret");
		login.setUserId(1L);

		mvc.perform(post("/api/rest/users/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(login)))
				.andDo(print()) 
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", is("Jon")));

		verify(this.usersService, times(1)).login(any());
		verify(this.usersService, times(1)).getUser(any());
	}
	
	@Test
	@DisplayName("#login with matched password and no user should return 401")
	void testLogin_matchedPasswordWithoutUser() throws Exception {
		when(this.usersService.login(any())).thenReturn(true);
		when(this.usersService.getUser(any())).thenReturn(Optional.empty());
		var login = new Login();
		login.setPassword("lorem ipsum doret");
		login.setUserId(1L);

		mvc.perform(post("/api/rest/users/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(login)))
				.andDo(print()) 
				.andExpect(status().isUnauthorized()); 

		verify(this.usersService, times(1)).login(any());
		verify(this.usersService, times(1)).getUser(any());
	}
}
