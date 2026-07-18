package com.ltfullstack.userservice.repository;

import com.ltfullstack.userservice.configuration.FeignConfig;
import com.ltfullstack.userservice.dto.identity.TokenExchangeParam;
import com.ltfullstack.userservice.dto.identity.TokenResponse;
import com.ltfullstack.userservice.dto.identity.UserCreationParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}", configuration = FeignConfig.class)
public interface IdentityClient {
    @PostMapping(
            value = "/realms/master/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenResponse exTokenExchangeParam(@RequestBody TokenExchangeParam formData);

    @PostMapping(
            value = "/admin/realms/laptrinhfullstack/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<?> CreaterUser(@RequestBody UserCreationParam param, @RequestHeader("authorization") String token);
}
