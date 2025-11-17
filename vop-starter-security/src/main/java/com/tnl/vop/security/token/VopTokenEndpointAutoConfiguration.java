package com.tnl.vop.security.token;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@AutoConfiguration
@EnableConfigurationProperties({TokenEndpointProperties.class, VopTokenProperties.class})
@Conditional(TokenEndpointProfileCondition.class)
public class VopTokenEndpointAutoConfiguration {

  @Bean
  @ConditionalOnProperty(
      prefix = "vop.security.token-endpoint",
      name = "enabled",
      havingValue = "true"
  )
  public TokenEndpointController tokenEndpointController(
      TokenEndpointProperties tokenEndpointProperties,
      VopTokenProperties vopTokenProperties
  ) {
    return new TokenEndpointController(tokenEndpointProperties, vopTokenProperties);
  }
}
