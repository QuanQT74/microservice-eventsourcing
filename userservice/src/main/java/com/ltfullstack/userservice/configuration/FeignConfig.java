package com.ltfullstack.userservice.configuration;

import feign.Client;
import feign.form.spring.SpringFormEncoder;
import feign.codec.Encoder;
import feign.httpclient.ApacheHttpClient;
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
    public Encoder formEncoder() {
        return new SpringFormEncoder();
    }
}
