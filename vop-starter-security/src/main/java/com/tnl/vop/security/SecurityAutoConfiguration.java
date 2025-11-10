package com.tnl.vop.security;

import com.tnl.vop.security.config.VopDefaultSecurityPaths;
import com.tnl.vop.security.config.VopWebSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.stream.Stream;

@AutoConfiguration
@EnableConfigurationProperties({ VopWebSecurityProperties.class, SecurityProperties.class })
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain vopSecurityFilterChain(
            HttpSecurity http,
            SecurityProperties props,
            JwtAuthoritiesConverter authoritiesConverter,
            ApiKeyAuthFilter apiKeyAuthFilter,
            VopWebSecurityProperties webProps
    ) throws Exception {

        String[] permitAll = Stream.concat(
                Arrays.stream(VopDefaultSecurityPaths.DEFAULT_PERMIT_ALL),
                webProps.getPermitPaths().stream()
        ).toArray(String[]::new);

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(permitAll).permitAll()
                        .anyRequest().authenticated()
                );

        var jwtConv = new JwtAuthenticationConverter();
        jwtConv.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        http.oauth2ResourceServer(oauth -> oauth.jwt(j -> j.jwtAuthenticationConverter(jwtConv)));

        if (props.getApiKey().isEnabled()) {
            http.addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);
        }

        http.httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public java.util.function.Function<Jwt, VopPrincipal> vopPrincipalExtractor(SecurityProperties props) {
        return jwt -> VopPrincipal.fromJwt(jwt, props);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthoritiesConverter.class)
    public JwtAuthoritiesConverter jwtAuthoritiesConverter(SecurityProperties props) {
        return new DefaultJwtAuthoritiesConverter(); // or pass props if needed
    }

    @Bean
    @ConditionalOnMissingBean(ApiKeyAuthFilter.class)
    public ApiKeyAuthFilter apiKeyAuthFilter(SecurityProperties props) {
        return new ApiKeyAuthFilter(props);
    }
}
