package com.tnl.vop.platform.openapi;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Controls OpenAPI behavior (enable/disable + global security requirement).
 * The API key *value* does NOT live here; that belongs under vop.security.api-key.
 * Header name is derived from SecurityProperties, with an optional override here.
 */
@Validated
@ConfigurationProperties(prefix = "vop.openapi")
public class VopOpenApiProperties {

  /** Whether to register OpenAPI beans at all. */
  private boolean enabled = true;

  /** Whether to add a global SecurityRequirement (so Swagger sends auth by default). */
  private boolean globalSecurity = true;

  /**
   * Optional override of the API key header label shown in Swagger.
   * If null/blank, the auto-config uses SecurityProperties.apiKey.header.
   */
  private String header;

  // ---- getters/setters ----

  public boolean isEnabled() {
    return enabled;
  }
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isGlobalSecurity() {
    return globalSecurity;
  }
  public void setGlobalSecurity(boolean globalSecurity) {
    this.globalSecurity = globalSecurity;
  }

  public String getHeader() {
    return header;
  }
  public void setHeader(String header) {
    this.header = header;
  }
}
