package com.paymybuddy.app.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;

@Service
public class KeyringService {
    private final Keyring keyring;
    private final Map<String, String> cache = new HashMap<>();

    public KeyringService() {
        try {
            this.keyring = Keyring.create();
        } catch (BackendNotSupportedException e) {
            throw new IllegalStateException("Aucun backend de trousseau disponible", e);
        }
    }

    public String getPassword(String service, String account) {
        String key = service + "/" + account;
        return cache.computeIfAbsent(key, k -> {
            try {
                String password = keyring.getPassword(service, account);
                if (password == null) {
                    throw new IllegalStateException(
                        "Mot de passe introuvable dans le keyring pour " + service + "/" + account
                    );
                }
                return password;
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Erreur lors de la récupération du mot de passe dans le keyring pour " + service + "/" + account,
                        e
                );
            }
        });
    }
}
