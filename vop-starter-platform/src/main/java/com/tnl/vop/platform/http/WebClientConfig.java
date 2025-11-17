package com.tnl.vop.platform.http;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Opinionated WebClient configuration:
 * - Applies VOP HTTP timeout defaults.
 * - Propagates correlationId via X-Correlation-Id header.
 */
@Configuration
@ConditionalOnClass(WebClient.class) // only activates if WebFlux is on the classpath
public class WebClientConfig {

  @Bean
  @ConditionalOnMissingBean(WebClient.Builder.class)
  public WebClient.Builder vopWebClientBuilder(VopHttpProperties httpProperties) {

    VopHttpProperties.TimeoutProperties timeout = httpProperties.getTimeout();

    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout.getConnectMs())
        .responseTimeout(Duration.ofMillis(timeout.getReadMs()));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .filter(correlationIdFilter());
  }

  private ExchangeFilterFunction correlationIdFilter() {
    return (request, next) -> {
      String cid = MDC.get("correlationId");
      if (cid == null) {
        return next.exchange(request);
      }

      ClientRequest mutated = ClientRequest.from(request)
          .header("X-Correlation-Id", cid)
          .build();

      return next.exchange(mutated);
    };
  }

  // Default WebClient bean that uses the above builder
  @Bean
  @ConditionalOnMissingBean(WebClient.class)
  public WebClient webClient(WebClient.Builder builder) {
    return builder.build();
  }
}
