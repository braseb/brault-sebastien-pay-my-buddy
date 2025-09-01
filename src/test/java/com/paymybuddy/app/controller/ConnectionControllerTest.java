package com.paymybuddy.app.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.app.exception.UserAppendConnectionError;
import com.paymybuddy.app.exception.UserNotFoundException;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.CustomUserDetailsService;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@WebMvcTest(ConnectionController.class)
public class ConnectionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private UserRepository userRepository;
    
    @MockitoBean
    private TransactionService transactionService;
    
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /connection should return connection view")
    void getConnectionPage() throws Exception {
        mockMvc.perform(get("/connection")
        	.with(user("john@example.com")))
                .andExpect(status().isOk())
                .andExpect(view().name("connection"));
    }

    @Test
    @DisplayName("POST /connection success should redirect with successMessage")
    void addUserConnectionSuccess() throws Exception {
        mockMvc.perform(post("/add_user_connection")
                        .with(user("john@example.com"))
        		.with(csrf())
                        .param("email", "john@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attribute("successMessage", "Connection append successfull !"));
    }

    @Test
    @DisplayName("POST /connection when user already connected should redirect with emailError")
    void addUserConnectionAlreadyExists() throws Exception {
        doThrow(new UserAppendConnectionError("User already connected"))
                .when(userService).appendConnectionUser(anyString());

        mockMvc.perform(post("/add_user_connection")
        		.with(user("john@example.com"))
        		.with(csrf())
                        .param("email", "john@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attribute("emailError", "User already connected"));
    }

    @Test
    @DisplayName("POST /add_user_connection invalid email should trigger ConstraintViolationException")
    void addUserConnectionInvalidEmail() throws Exception {
        // Simule une contrainte de validation manuelle (comme si @Email échouait)
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation.getMessage()).thenReturn("L'Email is not valid");
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        // Forcer userService à lancer une ConstraintViolationException
        doThrow(exception).when(userService).appendConnectionUser(anyString());

        mockMvc.perform(post("/add_user_connection")
                	.with(user("john@example.com"))        
                	.with(csrf())
                        .param("email", "invalid-email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attribute("emailError", "L'Email is not valid"));
    }

    @Test
    @DisplayName("POST /add_user_connection UserNotFoundException should redirect with emailError")
    void addUserConnectionUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found"))
                .when(userService).appendConnectionUser(anyString());

        mockMvc.perform(post("/add_user_connection")
        		.with(user("john@example.com"))
        		.with(csrf())
                        .param("email", "unknown@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attribute("emailError", "User not found"));
    }
    
}
