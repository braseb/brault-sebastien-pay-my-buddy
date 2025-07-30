package com.paymybuddy.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
public class CustomSpringSecurityConfig {
    
	//@Autowired
	//CustomUserDetailsService customUserDetailsService;
	
	
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> {
	            auth.requestMatchers("/login", "/register").permitAll()
	            	.requestMatchers("/style/**", "/images/**", "/js/**").permitAll()
	            	.anyRequest().authenticated();
				})
                //.formLogin(formLogin -> formLogin.defaultSuccessUrl("/transaction", true))//.permitAll())
                .formLogin(login -> login.loginPage("/login")
                					.usernameParameter("email")
                					.passwordParameter("password")
			                        .defaultSuccessUrl("/transaction", true)
			                        .permitAll())
			                		.logout(Customizer.withDefaults())
			                        .build();

    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    /*@Bean
    AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();

    }*/
    
    @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
