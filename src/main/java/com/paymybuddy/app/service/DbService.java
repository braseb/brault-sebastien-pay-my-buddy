package com.paymybuddy.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DbService {
    public DbService(@Value("${keyring:myApp/braseb}") String password) {
        System.out.println("Mot de passe injecté depuis keyring : " + password);
    }
}
