package com.paymybuddy.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
public class CustomSpringSecurityConfig {
    
		
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> {
	            auth.requestMatchers("/login", "/register").permitAll()
	                .requestMatchers("/style/**", "/images/**", "/js/**").permitAll()
	                .requestMatchers("/error/**", "/error-403", "/error-404", "/error-500").permitAll()
	            	.anyRequest().authenticated();
				})
                .formLogin(login -> login.loginPage("/login")
                					.usernameParameter("email")
                					.passwordParameter("password")
			                        .defaultSuccessUrl("/transaction", true)
			                        .permitAll())
			                		.build();

    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }
    
    @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
    
    
    
}
