package com.paymybuddy.app.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.app.dto.TransactionFormDto;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.CustomUserDetailsService;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private UserRepository userRepository;
    
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // --- GET /transaction ---
    @Test
    @DisplayName("GET /transaction should return transaction page with model attributes")
    void getTransactionPage() throws Exception {
        User user = new User("John", "john@mail.com", "pass");
        user.setId(1);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.getEmailFromConnectionUser(any())).thenReturn(List.of("friend@mail.com"));
        when(transactionService.getTransactionByUserId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transaction")
        	.with(user("john@mail.com")))
        	.andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("listEmail"))
                .andExpect(model().attributeExists("transactionFormDto"));

        verify(userService, times(1)).getCurrentUser();
        verify(transactionService, times(1)).getTransactionByUserId(1);
    }

    // --- POST /transaction success ---
    @Test
    @DisplayName("POST /transaction with valid dto should redirect to /transaction")
    void createTransactionSuccess() throws Exception {
        User user = new User("John", "john@mail.com", "pass");
        when(userService.getCurrentUser()).thenReturn(user);
    	mockMvc.perform(post("/transaction")
                            .with(user("john@mail.com"))
            		.with(csrf()) // CSRF obligatoire
                            .param("email", "friend@mail.com")
                            .param("description", "test transaction")
                            .param("amount", "10.0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/transaction"));

        verify(transactionService, times(1)).saveTransaction(any(TransactionFormDto.class));
    }

    // --- POST /transaction with errors ---
    @Test
    @DisplayName("POST /transaction with invalid dto should return transaction page")
    void createTransactionValidationErrors() throws Exception {
        User user = new User("John", "john@mail.com", "pass");
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.getEmailFromConnectionUser(any())).thenReturn(List.of("friend@mail.com"));
        when(transactionService.getTransactionByUserId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/transaction")
        		.with(user("john@mail.com"))
        		.with(csrf())
                        // Missing emailReceiver → devrait générer une erreur de validation
                        .param("description", "test transaction")
                        .param("amount", "10.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("listEmail"));

        verify(transactionService, never()).saveTransaction(any());
    }
}

