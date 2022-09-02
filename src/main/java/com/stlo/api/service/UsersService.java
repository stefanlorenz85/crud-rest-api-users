package com.stlo.api.service;

import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.stlo.api.config.SecurityConfig;
import com.stlo.api.model.Login;
import com.stlo.api.model.db.User;
import com.stlo.api.model.db.UserCredential;
import com.stlo.api.repository.CredentialsRepository;
import com.stlo.api.repository.UsersRepository;

@Service
public class UsersService {

	
	private final UsersRepository usersRepository;
	private final CredentialsRepository credentialsRepository;
	private final PasswordEncoder passwordEncoder;
	private final SecurityConfig securityConfig;

	public UsersService(
			final UsersRepository usersRepository,
			final CredentialsRepository credentialsRepository,
			final PasswordEncoder passwordEncoder,
			final SecurityConfig securityConfig) {

		Assert.notNull(usersRepository, UsersRepository.class + " must not be null");
		Assert.notNull(credentialsRepository, CredentialsRepository.class + " must not be null");
		Assert.notNull(passwordEncoder, PasswordEncoder.class + " must not be null");
		Assert.notNull(securityConfig, SecurityConfig.class + " must not be null");

		this.usersRepository = usersRepository;
		this.credentialsRepository = credentialsRepository;
		this.passwordEncoder = passwordEncoder;
		this.securityConfig = securityConfig;
	}

	@Transactional
	public User createUser(@NonNull User user) {
		Objects.requireNonNull(user);

		user = this.usersRepository.save(user);

		// default password, otherwise could take it from create request from user, or ...
		this.credentialsRepository.save(new UserCredential(user.getId(), this.passwordEncoder.encode(this.securityConfig.getDefaultPassword())));

		return user;
	}

	public Optional<User> getUser(@NonNull Long userId) {
		Objects.requireNonNull(userId);

		return this.usersRepository.findById(userId);
	}

	public void removeUser(@NonNull Long userId) {
		Objects.requireNonNull(userId);

		this.usersRepository.deleteById(userId);
	}

	public Page<User> getUserPage(@NonNull Pageable pageable) {
		Objects.requireNonNull(pageable);

		return this.usersRepository.findAll(pageable);
	}

	public boolean login(@NonNull Login login) {
		Objects.requireNonNull(login);

		return this.credentialsRepository.findByUserId(login.getUserId()).map(credentials -> {
			return this.passwordEncoder.matches(login.getPassword(), credentials.getPassword());
		}).orElse(false);
	}
}
