package com.tnl.vop.platform.http;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnClass(WebClient.class) // <- only activates if webflux is on classpath
public class WebClientConfig {

  @Bean
  @ConditionalOnMissingBean(WebClient.Builder.class)
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder()
        .filter((request, next) -> {
          String cid = MDC.get("correlationId");
          ClientRequest.Builder builder = ClientRequest.from(request);
          if (cid != null) {
            builder.header("X-Correlation-Id", cid);
          }
          return next.exchange(builder.build());
        });
  }

  // If you want a default WebClient too:
  @Bean
  @ConditionalOnMissingBean(WebClient.class)
  public WebClient webClient(WebClient.Builder builder) {
    return builder.build();
  }
}
