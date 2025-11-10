package com.tnl.vop.identity;

import com.tnl.vop.identity.claims.JwtClaimsIdentityClient;
import com.tnl.vop.identity.http.HttpIdentityClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties(VopIdentityProperties.class)
public class VopIdentityAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = "org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken")
    @ConditionalOnMissingBean
    public JwtClaimsIdentityClient jwtClaimsIdentityClient() {
        return new JwtClaimsIdentityClient();
    }

    @Bean
    @ConditionalOnProperty(prefix = "vop.identity", name = "http-enabled", havingValue = "true")
    public HttpIdentityClient httpIdentityClient(
            RestTemplateBuilder builder,
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            VopIdentityProperties props) {

        WebClient webClient = null;
        WebClient.Builder b = webClientBuilderProvider.getIfAvailable();
        if (b != null) {
            try { webClient = b.build(); } catch (Throwable ignored) {}
        }
        return new HttpIdentityClient(builder, webClient, props);
    }
}
