package com.stlo.api.model;

import javax.validation.constraints.NotNull;

public class Login {

	@NotNull
	private Long userId;

	@NotNull
	private String password;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
