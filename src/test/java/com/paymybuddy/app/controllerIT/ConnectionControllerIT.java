package com.paymybuddy.app.controllerIT;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class ConnectionControllerIT {
    
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User john;
    private User alice;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // nettoyage pour chaque test

        // Utilisateur connecté
        john = new User();
        john.setUsername("john");
        john.setEmail("john@mail.com");
        john.setPassword(passwordEncoder.encode("secret"));
        userRepository.save(john);

        // Utilisateur à ajouter en connexion
        alice = new User();
        alice.setUsername("alice");
        alice.setEmail("alice@mail.com");
        alice.setPassword(passwordEncoder.encode("secret"));
        userRepository.save(alice);
    }
    
    

    @Test
    @DisplayName("POST /connection success should redirect with successMessage")
    void testAddConnectionUserSuccess() throws Exception {
        mockMvc.perform(post("/add_user_connection")
        	.with(user(john.getEmail()))
        	.with(csrf())
                .param("email", "alice@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"));
        
        assertEquals(john.getConnectionUser(), userRepository.findByEmail("john@mail.com").orElseThrow().getConnectionUser());
    }
    
    @Test
    @DisplayName("POST /connection when user already connected should redirect with emailError")
    void testAddUserConnectionAlreadyExists() throws Exception {
        john.addConnectionUser(alice);
        userRepository.save(john);
	
	mockMvc.perform(post("/add_user_connection")
        		.with(user(john.getEmail()))
        		.with(csrf())
        		.param("email", alice.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attributeExists("emailError"));
                //.andExpect(flash().attribute("emailError", "The user with the mail " + john.getEmail() + " already have the user " + alice.getEmail() + " in his connection list"));
    }

    @Test
    @DisplayName("POST /add_user_connection invalid email should trigger ConstraintViolationException")
    void testAddUserConnectionInvalidEmail() throws Exception {
        
        mockMvc.perform(post("/add_user_connection")
        		.with(user(john.getEmail()))        
                	.with(csrf())
                        .param("email", "invalid-email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attributeExists("emailError"));
                //.andExpect(flash().attribute("emailError", "L'Email is not valid"));
        
        assertTrue(userRepository.findByEmail("john@mail.com").orElseThrow().getConnectionUser().isEmpty());
    }

    @Test
    @DisplayName("POST /add_user_connection UserNotFoundException should redirect with emailError")
    void testAddUserConnectionUserNotFound() throws Exception {
        mockMvc.perform(post("/add_user_connection")
        		.with(user(john.getEmail()))
        		.with(csrf())
                        .param("email", "unknown@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connection"))
                .andExpect(flash().attributeExists("emailError"));
                //.andExpect(flash().attribute("emailError", "The user with the email unknown@example.com is not found"));
        
        
        assertTrue(userRepository.findByEmail("john@mail.com").orElseThrow().getConnectionUser().isEmpty());
    }
}
