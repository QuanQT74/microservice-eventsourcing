package com.ltfullstack.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient

public class ApigatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApigatewayApplication.class, args);
	}

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174", "http://localhost:5175"));
		config.setMaxAge(3600L);
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		config.setAllowedHeaders(Arrays.asList("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}

	@Bean
	@Order(-100)
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeExchange(exchanges -> exchanges
				.pathMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
				.pathMatchers("/api/v1/users/me/**").permitAll()
				.pathMatchers("/api/v1/public/**").permitAll()
				.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.anyExchange().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.jwtDecoder(jwtDecoder())));
		return http.build();
	}

	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		String jwkSetUri = "http://keycloak:8080/realms/laptrinhfullstack/protocol/openid-connect/certs";
		return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}

	@Bean
	public KeyResolver ipKeyResolver(){
		return exchange -> Mono.just(
				exchange.getRequest()
						.getRemoteAddress()
						.getAddress()
						.getHostAddress()
		);
	}
}
