package com.paymybuddy.app.property;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.EnumerablePropertySource;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;

public class KeyringPropertySource extends EnumerablePropertySource<Keyring> {
    private final Map<String, String> cache = new HashMap<>();

    /*public KeyringPropertySource(String name) {
        super(name, createKeyring());
    }

    private static Keyring createKeyring() {
        try {
            return Keyring.create();
        } catch (BackendNotSupportedException e) {
            throw new IllegalStateException("Aucun backend de trousseau disponible", e);
        }
    }*/
 
    public KeyringPropertySource(String name) {
        super(name, KeyringHolder.INSTANCE); // <- nom du PropertySource et instance Keyring
    }
    
    // Utilise une instance unique de Keyring pour éviter la création de threads multiples
    private static class KeyringHolder {
        private static final Keyring INSTANCE = createKeyring();

        private static Keyring createKeyring() {
            try {
                return Keyring.create();
            } catch (BackendNotSupportedException e) {
                throw new IllegalStateException("Aucun backend de trousseau disponible", e);
            }
        }
    }

    @Override
    public String[] getPropertyNames() {
        
        return cache.keySet().toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        // Format attendu : keyring:service/account
        if (!name.startsWith("keyring:")) {
            return null;
        }
        return cache.computeIfAbsent(name, n -> {
            String[] parts = n.substring("keyring:".length()).split("/");
            if (parts.length != 2) return null;
            String service = parts[0];
            String account = parts[1];
            try {
                String password = source.getPassword(service, account);
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
