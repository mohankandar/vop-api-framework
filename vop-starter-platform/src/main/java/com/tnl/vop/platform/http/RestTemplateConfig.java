package com.tnl.vop.platform.http;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Opinionated RestTemplate configuration:
 * - Applies VOP HTTP timeout defaults.
 * - Propagates correlationId via X-Correlation-Id header.
 */
@Configuration
public class RestTemplateConfig {

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  public RestTemplate restTemplate(RestTemplateBuilder builder,
      VopHttpProperties httpProperties) {

    VopHttpProperties.TimeoutProperties timeout = httpProperties.getTimeout();

    Duration connectTimeout = Duration.ofMillis(timeout.getConnectMs());
    Duration readTimeout = Duration.ofMillis(timeout.getReadMs());

    return builder
        .setConnectTimeout(connectTimeout)
        .setReadTimeout(readTimeout)
        .additionalInterceptors((req, body, ex) -> {
          String cid = MDC.get("correlationId");
          if (cid != null) {
            req.getHeaders().add("X-Correlation-Id", cid);
          }
          return ex.execute(req, body);
        })
        .build();
  }
}
