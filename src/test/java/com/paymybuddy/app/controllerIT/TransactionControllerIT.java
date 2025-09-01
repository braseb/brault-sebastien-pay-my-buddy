package com.paymybuddy.app.controllerIT;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.*;
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

import com.paymybuddy.app.dto.TransactionFormDto;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.projection.TransactionProjection;
import com.paymybuddy.app.repository.TransactionRepository;
import com.paymybuddy.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class TransactionControllerIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User john;
    private User alice;
    
    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // on nettoie la base avant chaque test
        transactionRepository.deleteAll();
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
        
        john.addConnectionUser(alice);
        userRepository.save(john);
        
        Transaction transaction = new Transaction();
        transaction.setDescription("test1");
        transaction.setAmount(10.0);
        transaction.setUserReceiver(alice);
        transaction.setUserSender(john);
        
        
        transactionRepository.save(transaction);
        
    }
    
    // --- GET /transaction ---
    @Test
    @DisplayName("GET /transaction should return transaction page with model attributes")
    void getTransactionPage() throws Exception {
        
        mockMvc.perform(get("/transaction")
            .with(user("john@mail.com")))
            .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attribute("transactions", hasItem(allOf(
                        hasProperty("username", equalTo("alice")),
                        hasProperty("description", equalTo("test1")),
                        hasProperty("amount", equalTo(10.0))))))
                .andExpect(model().attributeExists("listEmail"))
                .andExpect(model().attributeExists("transactionFormDto"));
    }
    
 // --- POST /transaction success ---
    @Test
    @DisplayName("POST /transaction with valid dto should redirect to /transaction")
    void createTransactionSuccess() throws Exception {
    
        mockMvc.perform(post("/transaction")
                            .with(user("john@mail.com"))
                    .with(csrf()) // CSRF obligatoire
                            .param("email", "alice@mail.com")
                            .param("description", "test2")
                            .param("amount", "20.0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/transaction"));
        
        User userBdd = userRepository.findByEmail("john@mail.com").orElseThrow();
        
        List<TransactionProjection> saved = transactionRepository.findByUserSenderId(userBdd.getId());
        TransactionProjection savedTx = saved.stream()
                                        .filter(t -> t.getAmount().equals(20.0))
                                        .findFirst()
                                        .orElseThrow();      
        assertEquals(20.0, savedTx.getAmount());
        assertEquals( "test2", savedTx.getDescription());
        assertEquals("alice", savedTx.getUsername());
        
    }

    // --- POST /transaction with errors ---
    @Test
    @DisplayName("POST /transaction with invalid dto should return transaction page")
    void createTransactionValidationErrors() throws Exception {
        
        mockMvc.perform(post("/transaction")
                .with(user("john@mail.com"))
                .with(csrf())
                        // Missing emailReceiver → devrait générer une erreur de validation
                        .param("description", "")
                        .param("amount", "0")) // valeur à 0 -> erreur
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("listEmail"))
                .andExpect(model().attributeHasFieldErrors("transactionFormDto", "email", "amount"));

        
    }
    
}
