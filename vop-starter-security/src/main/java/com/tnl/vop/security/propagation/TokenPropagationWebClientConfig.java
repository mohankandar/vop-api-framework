package com.tnl.vop.security.propagation;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@ConditionalOnClass({WebClient.class, ExchangeFilterFunction.class})
public class TokenPropagationWebClientConfig {

    @Bean
    public ExchangeFilterFunction bearerPropagationFilter() {
        return (request, next) -> {
            var ctx = SecurityContextHolder.getContext();
            var auth = ctx == null ? null : ctx.getAuthentication();
            String token = (auth != null && auth.getCredentials() != null)
                    ? String.valueOf(auth.getCredentials()) : null;

            ClientRequest mutated = (token != null && !token.isBlank())
                    ? ClientRequest
                    .from(request)
                    .headers(h -> h.set(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .build()
                    : request;

            return next.exchange(mutated);
        };
    }
}
