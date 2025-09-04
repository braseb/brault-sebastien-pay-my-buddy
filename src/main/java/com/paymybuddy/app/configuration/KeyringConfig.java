package com.paymybuddy.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import com.paymybuddy.app.property.KeyringPropertySource;

import jakarta.annotation.PostConstruct;

@Configuration
public class KeyringConfig {
    private final ConfigurableEnvironment environment;

    public KeyringConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        environment.getPropertySources().addFirst(new KeyringPropertySource("keyring"));
    }
}
