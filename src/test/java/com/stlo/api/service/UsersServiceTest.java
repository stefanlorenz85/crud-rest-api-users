package com.stlo.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.stlo.api.config.SecurityConfig;
import com.stlo.api.model.Login;
import com.stlo.api.model.db.User;
import com.stlo.api.model.db.UserCredential;
import com.stlo.api.repository.CredentialsRepository;
import com.stlo.api.repository.UsersRepository;

@SpringBootTest(classes = UsersService.class)
public class UsersServiceTest {

	@Autowired
	UsersService usersService;

	@MockBean
	UsersRepository usersRepository;

	@MockBean
	CredentialsRepository credentialsRepository;

	@MockBean
	PasswordEncoder passwordEncoder;

	@MockBean
	SecurityConfig securityConfig;

	@Test
	@DisplayName("#createUser should save user and credentials and return user")
	void testCreateUser() {
		var captorCredential = ArgumentCaptor.forClass(UserCredential.class);
		var captorUser = ArgumentCaptor.forClass(User.class);
		var user = new User();
		when(this.usersRepository.save(user)).thenReturn(user);
		when(this.passwordEncoder.encode(any())).thenReturn("password");
		var credential = new UserCredential(null, "password");

		var result = this.usersService.createUser(user);

		assertThat(result).isEqualTo(user);
		verify(this.credentialsRepository, times(1)).save(captorCredential.capture());
		verify(this.usersRepository, times(1)).save(captorUser.capture());
		assertThat(captorCredential.getValue()).usingRecursiveComparison().isEqualTo(credential);
		assertThat(captorUser.getValue()).usingRecursiveComparison().isEqualTo(user);
	}

	@Test
	@DisplayName("#createUser with null should throw null pointer expection")
	void testCreateUser_withNull() {

		Assertions.assertThrows(NullPointerException.class, () -> {

			this.usersService.createUser(null);

			verify(this.usersRepository, times(0)).save(any());
			verify(this.credentialsRepository, times(0)).save(any());
		});
	}

	@Test
	@DisplayName("#getUser should call repository and return user")
	void testGetUser() {
		var user = Optional.of(new User());
		when(this.usersRepository.findById(123L)).thenReturn(user);

		var result = this.usersService.getUser(123L);

		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("#getUser with null should throw null pointer expection")
	void testGetUser_withNull() {

		Assertions.assertThrows(NullPointerException.class, () -> {

			this.usersService.getUser(null);

			verify(this.usersRepository, times(0));
		});
	}

	@Test
	@DisplayName("#removeUser should call repository")
	void testRemoveUser() {

		this.usersService.removeUser(123L);

		verify(this.usersRepository, times(1)).deleteById(123L);
	}

	@Test
	@DisplayName("#getUser with null should throw null pointer expection")
	void testRemoveUser_withNull() {

		Assertions.assertThrows(NullPointerException.class, () -> {

			this.usersService.removeUser(null);

			verify(this.usersRepository, times(0));
		});
	}

	@Test
	@DisplayName("#getUserPage should call repository with pageable and return users page")
	void testGetUserPage() {
		Page<User> expected = Page.empty();
		var pageable = PageRequest.of(0, 10);
		when(this.usersRepository.findAll(pageable)).thenReturn(expected);

		var result = this.usersService.getUserPage(pageable);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	@DisplayName("#getUserPage with null should throw null pointer expection")
	void testGetUserPage_withNull() {

		Assertions.assertThrows(NullPointerException.class, () -> {

			this.usersService.getUserPage(null);

			verify(this.usersRepository, times(0));
		});
	}

	@Test
	@DisplayName("#login with matching password ")
	void testLogin_matchingPassword() {
		var login  = new Login();
		login.setPassword("abc");
		login.setUserId(1L);
		when(this.credentialsRepository.findByUserId(1L)).thenReturn(Optional.of(new UserCredential(1L, "")));
		when(this.passwordEncoder.matches(any(), any())).thenReturn(true);
		
		var result = this.usersService.login(login);

		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("#login with unmatching password ")
	void testLogin_unmatchingPassword() {
		var login  = new Login();
		login.setPassword("abc");
		login.setUserId(1L);
		when(this.credentialsRepository.findByUserId(1L)).thenReturn(Optional.of(new UserCredential(1L, "")));
		when(this.passwordEncoder.matches(any(), any())).thenReturn(false);
		
		var result = this.usersService.login(login);

		assertThat(result).isFalse();
	}
}
