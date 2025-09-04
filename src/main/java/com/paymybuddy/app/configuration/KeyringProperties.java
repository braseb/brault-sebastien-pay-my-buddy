package com.paymybuddy.app.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keyring.datasource")
public class KeyringProperties {
    private String service;
    private String account;

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
}
