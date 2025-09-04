package com.paymybuddy.app.controllerIT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import com.paymybuddy.app.model.User;
import com.paymybuddy.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class UserControllerIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User john;
    
    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // on nettoie la base avant chaque test
     // Utilisateur connecté
        john = new User();
        john.setUsername("john");
        john.setEmail("john@mail.com");
        john.setPassword(passwordEncoder.encode("secret"));
        userRepository.save(john);
    }
    
 // /profile GET
    @Test
    @DisplayName("GET /profile should return profile view with userUpdateDto")
    void profileView() throws Exception {
        
	
        mockMvc.perform(get("/profile")
        		.with(user(john.getEmail())))
        		.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("userUpdateDto"))
                .andExpect(model().attribute("userUpdateDto", 
                        Matchers.hasProperty("email", Matchers.equalTo("john@mail.com"))))
                    .andExpect(model().attribute("userUpdateDto", 
                        Matchers.hasProperty("username", Matchers.equalTo("john"))));
        
        
        
    }
    
 // /profile PUT success without password change
    @Test
    @DisplayName("PUT /profile with valid dto and no password change should redirect to /profile")
    void updateProfileSuccessNoPasswordChange() throws Exception {
    	    	
    	mockMvc.perform(put("/profile")
                                .with(user(john.getEmail()))
                                .with(csrf())
                                .param("username", "johnNouveau")
                                .param("email", "johnNouveau@mail.com")
                                .param("oldPassword", "")
                                .param("newPassword", ""))
                        .andDo(print())        
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/profile"));
                        
    	assertEquals("johnNouveau@mail.com", userRepository.findByEmail("johnNouveau@mail.com").orElseThrow().getEmail());
    	assertEquals("johnNouveau", userRepository.findByEmail(john.getEmail()).orElseThrow().getUsername());
    	
    }
    
 // /profile PUT success with password change
    @Test
    @DisplayName("PUT /profile with valid dto and no password change should redirect to /profile")
    void updateProfileSuccessWithPasswordChange() throws Exception {
                
        mockMvc.perform(put("/profile")
                                .with(user(john.getEmail()))
                                .with(csrf())
                                .param("username", "johnNouveau")
                                .param("email", "johnNouveau@mail.com")
                                .param("oldPassword", "secret")
                                .param("newPassword", "secret1"))
                        .andDo(print())        
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/profile"));
                        
        assertEquals("johnNouveau@mail.com", userRepository.findByEmail("johnNouveau@mail.com").orElseThrow().getEmail());
        assertEquals("johnNouveau", userRepository.findByEmail(john.getEmail()).orElseThrow().getUsername());
        
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
        
        assertEquals(john.getEmail(), userRepository.findByEmail(john.getEmail()).orElseThrow().getEmail());
    }

    @Test
    @DisplayName("POST /register success should redirect with successMessage")
    void testRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "alice")
                .param("email", "alice@mail.com")
                .param("password", "secret"))
                .andDo(print())
        	.andExpect(flash().attributeExists("successCreate"))
        	.andExpect(status().is3xxRedirection())
        	.andExpect(redirectedUrl("/login"));
                
        User user = userRepository.findByEmail("alice@mail.com").orElseThrow();
        assertEquals("alice", user.getUsername());
    }
    
 // /register POST error
    @Test
    @DisplayName("POST /register with invalid email should return register view")
    void registerValidationError() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "alice")
                        .param("email", "bad-email") //email non valide
                        .param("password", "secret"))
                .andDo(print())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("userDto", "email"));
        
        User user = userRepository.findByEmail("alice@mail.com").orElse(null);
        assertNull(user);
    }
    
 // ExceptionHandler IllegalArgumentException
    @Test
    @DisplayName("Should redirect to /profile when IllegalArgumentException is thrown")
    void handleUserNotFoundException() throws Exception {
       mockMvc.perform(put("/profile")
                    .with(user("john@mail.com"))
                    .with(csrf())
                        .param("username", "john")
                        .param("email", "john@mail.com")
                        .param("newPassword", "123")
                        .param("oldPassword", "123")
                        .param("from", "profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("globalError"))
                .andExpect(flash().attribute("globalError", "The old password is not correct"))
                .andExpect(redirectedUrl("/profile"));
       
       
        
        
    }

    // ExceptionHandler UserAlreadyExist
    @Test
    @DisplayName("/register Should redirect to /register when UserAlreadyExistException is thrown")
    void handleUserAlreadyExistException() throws Exception {
        
        mockMvc.perform(post("/register")
                    .with(csrf())
                        .param("username", john.getUsername())
                        .param("email", john.getEmail())
                        .param("password", john.getPassword())
                        .param("from", "register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("globalError"))
                .andExpect(flash().attribute("globalError", "The user with the email " + john.getEmail() + " already exist"))
                .andExpect(redirectedUrl("/register"));
        
        
    }
    
}
