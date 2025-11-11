package com.tnl.vop.security;

import com.tnl.vop.security.config.VopWebSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@EnableConfigurationProperties({VopWebSecurityProperties.class, SecurityProperties.class})
public class SecurityAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SecurityFilterChain vopSecurityFilterChain(
      HttpSecurity http,
      SecurityProperties props,
      JwtAuthoritiesConverter authoritiesConverter,
      ApiKeyAuthFilter apiKeyAuthFilter,
      VopWebSecurityProperties webProps,
      org.springframework.beans.factory.ObjectProvider<org.springframework.security.oauth2.jwt.JwtDecoder> jwtDecoderProvider
  ) throws Exception {

    String[] permitAll = java.util.stream.Stream.concat(
        java.util.Arrays.stream(
            com.tnl.vop.security.config.VopDefaultSecurityPaths.DEFAULT_PERMIT_ALL),
        webProps.getPermitPaths().stream()
    ).toArray(String[]::new);

    http.csrf(
            org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(
            org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(org.springframework.boot.autoconfigure.security.servlet.PathRequest
                .toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(permitAll).permitAll()
            .anyRequest().authenticated()
        );

    // Configure JWT only if a JwtDecoder is present
    var jwtConv = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
    jwtConv.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

    var decoder = jwtDecoderProvider.getIfAvailable();
    if (decoder != null) {
      http.oauth2ResourceServer(oauth ->
          oauth.jwt(j -> j
              .jwtAuthenticationConverter(jwtConv)
              .decoder(decoder)
          )
      );
    }
    // else: no JwtDecoder bean -> skip configuring oauth2 resource server (API key can stand alone)

    if (props.getApiKey().isEnabled()) {
      http.addFilterBefore(apiKeyAuthFilter,
          org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    }

    http.httpBasic(org.springframework.security.config.Customizer.withDefaults())
        .formLogin(
            org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable)
        .logout(
            org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public java.util.function.Function<Jwt, VopPrincipal> vopPrincipalExtractor(
      SecurityProperties props) {
    return jwt -> VopPrincipal.fromJwt(jwt, props);
  }

  @Bean
  @ConditionalOnMissingBean(JwtAuthoritiesConverter.class)
  public JwtAuthoritiesConverter jwtAuthoritiesConverter(SecurityProperties props) {
    return new DefaultJwtAuthoritiesConverter(props);
  }

  @Bean
  @ConditionalOnMissingBean(ApiKeyAuthFilter.class)
  public ApiKeyAuthFilter apiKeyAuthFilter(SecurityProperties props) {
    var f = new ApiKeyAuthFilter(props);
    // no-op AuthenticationManager for pre-auth tokens
    f.setAuthenticationManager(auth -> auth);
    return f;
  }
}
