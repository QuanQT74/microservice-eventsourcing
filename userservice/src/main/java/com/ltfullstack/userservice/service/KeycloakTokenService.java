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

    @Value("${idp.realm}")
    private String realm;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = idpUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                TokenResponse.class
        );

        log.info("TOKEN RESPONSE: {}", response.getBody());
        return response.getBody().getAccessToken();
    }
}
