package com.example.cryptotrading.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("client.huobi.endpoints")
public class HuobiEndpointProperties {
    @NotNull
    private EndpointProperties bestPrices;
}