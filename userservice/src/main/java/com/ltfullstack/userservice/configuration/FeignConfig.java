package com.ltfullstack.userservice.configuration;

import feign.Client;
import feign.codec.Encoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonEncoder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Client feignClient() {
        return new ApacheHttpClient(HttpClients.createDefault());
    }

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder();
    }
}
