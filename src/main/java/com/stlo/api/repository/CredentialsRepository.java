package com.stlo.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stlo.api.model.db.UserCredential;

public interface CredentialsRepository extends JpaRepository<UserCredential, Long> {
	Optional<UserCredential> findByUserId(Long userId);
}
