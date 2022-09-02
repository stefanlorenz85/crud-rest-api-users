package com.stlo.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Value("${security.credentials.password}")
	private String defaultPassword;

	@Value("${security.disabled}")
	private boolean securityDisabled;

	public String getDefaultPassword() {
		return defaultPassword;
	}

	@Bean 
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		if(this.securityDisabled) {
			return http.csrf().disable().headers().disable().build();
		}
		return http.build();
	}
}