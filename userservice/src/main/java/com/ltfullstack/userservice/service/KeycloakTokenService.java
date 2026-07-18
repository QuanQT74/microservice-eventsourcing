package com.ltfullstack.userservice.service;

import com.ltfullstack.userservice.dto.identity.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class KeycloakTokenService {

    @Value("${idp.url}")
    private String idpUrl;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", adminUsername);
        body.add("password", adminPassword);
        body.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Use master realm for admin access
        String url = idpUrl + "/realms/master/protocol/openid-connect/token";
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                TokenResponse.class
        );

        log.info("ADMIN TOKEN obtained successfully");
        return response.getBody().getAccessToken();
    }
}
