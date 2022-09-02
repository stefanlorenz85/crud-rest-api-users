package com.stlo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stlo.api.model.db.User;

public interface UsersRepository extends JpaRepository<User, Long> {
}
