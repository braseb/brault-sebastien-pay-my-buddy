package com.paymybuddy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.app.dto.UserUpdateDto;
import com.paymybuddy.app.exception.UserAlreadyExistException;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.repository.UserRepository;
import com.paymybuddy.app.service.CustomUserDetailsService;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;

@WebMvcTest(controllers = UserController.class, 
			excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
//@SpringBootTest
//@AutoConfigureMockMvc

public class UserControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private TransactionService transactionService;
        
    @MockitoBean
    private UserRepository userRepository;
    
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    
    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;
    
    
    
    // /login
    @Test
    @DisplayName("GET /login should return login view")
    void loginView() throws Exception {
        mockMvc.perform(get("/login"))
        		.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
    
    // /profile GET
    @Test
    @DisplayName("GET /profile should return profile view with userUpdateDto")
    void profileView() throws Exception {
        when(userService.getCurrentUser())
                .thenReturn(new User("john", "john@mail.com", "pwd"));

        mockMvc.perform(get("/profile")
        		.with(user("john@mail.com")))
        		.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("userUpdateDto"));
        
        verify(userService, times(1)).getCurrentUser();
    }

    // /profile PUT success
    @Test
    @DisplayName("PUT /profile with valid dto should redirect to /profile")
    void updateProfileSuccess() throws Exception {
    	    	
    	mockMvc.perform(put("/profile")
        				.with(user("john@mail.com"))
        				.with(csrf())
        				.param("username", "john")
                        .param("email", "john@mail.com")
                        .param("oldPassword", "1234")
                        .param("newPassword", "1234"))
        		.andDo(print())        
        		.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    	
    	verify(userService, times(1)).updateUser(any(UserUpdateDto.class));
    }
    
 

    //  /profile PUT with validation error
    @Test
    @DisplayName("PUT /profile with invalid email should return profile view with error")
    void updateProfileValidationError() throws Exception {
        mockMvc.perform(put("/profile")
        				.with(user("john@mail.com"))
        				.with(csrf())
        				.param("username", "john")
                        .param("email", "bad-email")) //email non valide
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeHasFieldErrors("userUpdateDto", "email"));
        
        verify(userService, never()).updateUser(any(UserUpdateDto.class));
    }

    // /register GET
    @Test
    @DisplayName("GET /register should return register view")
    void registerView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    // /register POST success
    @Test
    @DisplayName("POST /register with valid dto should redirect to /login")
    void registerSuccess() throws Exception {
        mockMvc.perform(post("/register")
        				.with(csrf())
        				.param("username", "john")
                        .param("email", "john@mail.com")
                        .param("password", "secret"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successCreate"))
                
                .andExpect(redirectedUrl("/login"));
        
        verify(userService, times(1)).createUser(any(User.class));
    }

    // /register POST error
    @Test
    @DisplayName("POST /register with invalid email should return register view")
    void registerValidationError() throws Exception {
        mockMvc.perform(post("/register")
        				.with(csrf())
                        .param("username", "john")
                        .param("email", "bad-email") //email non valide
                        .param("password", "secret"))
                .andDo(print())
        		.andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userDto", "email"));
        
        verify(userService, never()).createUser(any(User.class));
    }

    // ExceptionHandler IllegalArgumentException
    @Test
    @DisplayName("Should redirect to /profile when IllegalArgumentException is thrown")
    void handleUserNotFoundException() throws Exception {
    	    	
    	when(userService.getCurrentUser()).thenReturn(new User("john", "john@mail.com", 
                "badOldPassword"));
    	
    	doThrow(new IllegalArgumentException("The old password is not correct"))
        .when(userService).updateUser(any(UserUpdateDto.class));

        mockMvc.perform(post("/profile")
    				.with(user("john@mail.com"))
    				.with(csrf())
    				    .param("_method", "put")  // nécessaire pour HiddenHttpMethodFilter
                        .param("username", "john")
                        .param("email", "john@mail.com")
                        .param("newPassword", "123")
                        .param("oldPassword", "123"))
                .andExpect(status().is3xxRedirection())
        		.andExpect(flash().attributeExists("globalError"))
        		.andExpect(flash().attribute("globalError", "The old password is not correct"))
                .andExpect(redirectedUrl("/profile"));
        
        
    }

    // ExceptionHandler UserAlreadyExist
    @Test
    @DisplayName("Should redirect to /register when UserAlreadyExistException is thrown")
    void handleUserAlreadyExistException() throws Exception {
        doThrow(new UserAlreadyExistException("Already exists"))
                .when(userService).createUser(any(User.class));

        mockMvc.perform(post("/register")
        			.with(csrf())
                        .param("username", "john")
                        .param("email", "john@mail.com")
                        .param("password", "secret")
                        .param("from", "register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("globalError"))
                .andExpect(flash().attribute("globalError", "Already exists"))
                .andExpect(redirectedUrl("/register"));
        
        
    }
}
