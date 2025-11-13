package com.tnl.vop.security;

import com.tnl.vop.security.config.VopDefaultSecurityPaths;
import com.tnl.vop.security.config.VopWebSecurityProperties;
import com.tnl.vop.security.token.TokenEndpointProperties;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
@EnableMethodSecurity
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
      ObjectProvider<JwtDecoder> jwtDecoderProvider,
      @Qualifier("vopMdcUserEnricherFilter")
      ObjectProvider<OncePerRequestFilter> mdcFilterProvider,
      BearerTokenResolver bearerTokenResolver
  ) throws Exception {

    // Permit list (framework defaults + app additions)
    String[] permitAll = java.util.stream.Stream.concat(
        Arrays.stream(VopDefaultSecurityPaths.DEFAULT_PERMIT_ALL),
        webProps.getPermitPaths().stream()
    ).toArray(String[]::new);

    http
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(
            h -> h.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(permitAll).permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(h -> h.disable())
        .formLogin(f -> f.disable())
        .logout(l -> l.disable());

    // JWT resource server (only if a decoder is present)
    var jwtConv = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
    jwtConv.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

    var decoder = jwtDecoderProvider.getIfAvailable();

    if (decoder != null) {
      http.oauth2ResourceServer(oauth -> oauth
          // <-- IMPORTANT: resolver is set on oauth, not on jwt()
          .bearerTokenResolver(bearerTokenResolver)
          .jwt(jwt -> jwt
              .jwtAuthenticationConverter(jwtConv)
              .decoder(decoder)
          )
      );
    }

    // API Key filter – runs before UsernamePasswordAuthenticationFilter
    http.addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);

    // MDC user filter (if platform starter is on classpath)
    OncePerRequestFilter mdcFilter = mdcFilterProvider.getIfAvailable();
    if (mdcFilter != null) {
      http.addFilterAfter(mdcFilter, BearerTokenAuthenticationFilter.class);
    }

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

    // Mark successful API-key auth as authenticated and grant a role.
    f.setAuthenticationManager(authentication -> {
      var pre = (org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken) authentication;

      // Default role for API-key callers (make configurable later if you want)
      var authorities = org.springframework.security.core.authority.AuthorityUtils.createAuthorityList(
          "ROLE_API");

      var authed = new org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken(
          pre.getPrincipal(), pre.getCredentials(), authorities);
      authed.setDetails(pre.getDetails());
      return authed; // <-- Authenticated = true, with ROLE_API
    });

    return f;
  }

  @Bean
  @ConditionalOnMissingBean(JwtDecoder.class)
  public JwtDecoder vopJwtDecoder(TokenEndpointProperties props) {
    // Use the same secret the token endpoint uses (HS256 for now)
    byte[] keyBytes = props.getHmacSecret().getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      throw new IllegalStateException(
          "vop.security.token-endpoint.hmac-secret must be >= 32 bytes");
    }
    var key = new SecretKeySpec(keyBytes, "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  }

  @Bean
  @ConditionalOnMissingBean(BearerTokenResolver.class)
  public BearerTokenResolver vopBearerTokenResolver(VopWebSecurityProperties webProps) {
    // Use the same permit-paths the app already configures
    List<RequestMatcher> permitMatchers = webProps.getPermitPaths().stream()
        .map(AntPathRequestMatcher::new)
        .collect(Collectors.toList());

    DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();

    return request -> {
      // If this request is on a permit path, pretend there is NO bearer token
      for (RequestMatcher m : permitMatchers) {
        if (m.matches(request)) {
          return null;
        }
      }
      // Otherwise, behave like normal
      return delegate.resolve(request);
    };
  }

}
