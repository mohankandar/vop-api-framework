package com.tnl.vop.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configurable settings for the internal token endpoint.
 * Disabled by default; can be enabled explicitly for local/dev environments.
 */
@ConfigurationProperties(prefix = "vop.security.token-endpoint")
public class TokenEndpointProperties {

  /**
   * Whether the token endpoint is enabled.
   * <p>
   * Defaults to {@code false} so that the endpoint is never exposed unless an
   * application explicitly opts in (typically only in local/dev).
   */
  private boolean enabled = false;          // default OFF

  /**
   * Token expiry in seconds. Default is 15 minutes.
   */
  private long expirySeconds = 900;        // default 15 minutes

  /**
   * Issuer value to embed in the JWT (iss claim).
   */
  private String issuer = "vop-token-endpoint";

  /**
   * HMAC secret used to sign the JWT.
   * <p>
   * MUST be provided via external configuration (env, vault, etc.) when endpoint is enabled.
   */
  private String hmacSecret;

  /**
   * Optional key id (kid) to include in JWT header.
   */
  private String kid;

  /**
   * Comma-separated list of Spring profiles under which the token endpoint is allowed
   * to be exposed. Default is {@code "local,dev"}.
   * <p>
   * Even if {@link #enabled} is {@code true}, the endpoint will only be created when
   * the active profiles intersect with this list.
   */
  private String allowedProfiles = "local,dev";

  /**
   * List of CIDR ranges allowed to call the endpoint. Empty = no CIDR restriction.
   */
  private List<String> allowedCidrs = new ArrayList<>();

  // getters/setters …

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public long getExpirySeconds() {
    return expirySeconds;
  }

  public void setExpirySeconds(long expirySeconds) {
    this.expirySeconds = expirySeconds;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getHmacSecret() {
    return hmacSecret;
  }

  public void setHmacSecret(String hmacSecret) {
    this.hmacSecret = hmacSecret;
  }

  public String getKid() {
    return kid;
  }

  public void setKid(String kid) {
    this.kid = kid;
  }

  public String getAllowedProfiles() {
    return allowedProfiles;
  }

  public void setAllowedProfiles(String allowedProfiles) {
    this.allowedProfiles = allowedProfiles;
  }

  public List<String> getAllowedCidrs() {
    return allowedCidrs;
  }

  public void setAllowedCidrs(List<String> allowedCidrs) {
    this.allowedCidrs = allowedCidrs;
  }
}
