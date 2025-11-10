package com.tnl.vop.security.propagation;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnClass({RestTemplate.class, RestTemplateBuilder.class})
public class TokenPropagationRestTemplateConfig {

    @Bean
    public RestTemplate restTemplateWithBearer(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors((req, body, ex) -> {
                    var ctx = SecurityContextHolder.getContext();
                    var auth = ctx == null ? null : ctx.getAuthentication();
                    String token = (auth != null && auth.getCredentials() != null)
                            ? String.valueOf(auth.getCredentials()) : null;
                    if (token != null && !token.isBlank()) {
                        req.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                    return ex.execute(req, body);
                })
                .build();
    }
}
