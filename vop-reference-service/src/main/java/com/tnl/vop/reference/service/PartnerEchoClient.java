package com.tnl.vop.reference.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PartnerEchoClient {

  private final WebClient webClient;

  @Value("http://localhost:${server.port:8080}")
  private String baseUrl;

  public PartnerEchoClient(
      WebClient.Builder builder,
      @Value("${reference.partner.echo-base-url}") String baseUrl
  ) {
    this.webClient = builder.baseUrl(baseUrl).build();
  }

  public ResponseEntity<String> callEcho() {
    return webClient.get()
        .uri("/internal/echo")
        .retrieve()
        .toEntity(String.class)
        .block();
  }
}
