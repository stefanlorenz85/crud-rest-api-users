package com.stlo.api.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stlo.api.exception.NotFoundException;
import com.stlo.api.exception.UnauthorizedException;
import com.stlo.api.model.Login;
import com.stlo.api.model.db.User;
import com.stlo.api.service.UsersService;

@RestController()
@RequestMapping("/api/rest/users")
public class UsersController {

	final UsersService usersService;

	public UsersController(final UsersService usersService) {
		Assert.notNull(usersService, UsersService.class + " must not be null");
		this.usersService = usersService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<User> createUser(@Valid @RequestBody User user) {

		return ResponseEntity.ok(this.usersService.createUser(user));
	}

	@GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<User> getUser(@PathVariable("userId") Long userId) {

		return ResponseEntity.ok(this.usersService.getUser(userId).orElseThrow(() -> new NotFoundException()));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Page<User>> getUserPage(@PageableDefault(size = 20, page = 0) Pageable pageable) {

		return ResponseEntity.ok(this.usersService.getUserPage(pageable));
	}

	@DeleteMapping(path = "/{userId}")
	ResponseEntity<Object> removeUser(@PathVariable("userId") Long userId) {

		this.usersService.removeUser(userId);

		return ResponseEntity.noContent().build();
	}

	@PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<User> loginUser(@Valid @RequestBody Login login) {

		if (this.usersService.login(login)) {
			return ResponseEntity
					.ok(this.usersService.getUser(login.getUserId()).orElseThrow(() -> new UnauthorizedException()));
		}
		throw new UnauthorizedException();
	}
}
