package com.paymybuddy.app.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paymybuddy.app.service.KeyringService;

@Configuration
public class DataSourceConfig {
    private final KeyringService keyringService;
    private final KeyringProperties keyringProperties;

    public DataSourceConfig(KeyringService keyringService, KeyringProperties keyringProperties) {
        this.keyringService = keyringService;
        this.keyringProperties = keyringProperties;
    }

    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    DataSource dataSource() {
        String username = keyringProperties.getAccount(); // account and password get from Keyring
        String password = keyringService.getPassword(
                keyringProperties.getService(),
                keyringProperties.getAccount()
        );

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
