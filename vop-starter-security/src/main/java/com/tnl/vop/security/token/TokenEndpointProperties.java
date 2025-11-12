package com.tnl.vop.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configurable settings for /api/token endpoint.
 * Enabled by default with a 15-minute expiry.
 */
@ConfigurationProperties(prefix = "vop.security.token-endpoint")
public class TokenEndpointProperties {

  private boolean enabled = true;          // default ON
  private long expirySeconds = 900;        // default 15 minutes
  private String issuer = "vop";
  private String hmacSecret = "please-change-me-32-bytes-minimum-12345678";
  private String kid;
  private List<String> allowedCidrs = new ArrayList<>();

  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }

  public long getExpirySeconds() { return expirySeconds; }
  public void setExpirySeconds(long expirySeconds) { this.expirySeconds = expirySeconds; }

  public String getIssuer() { return issuer; }
  public void setIssuer(String issuer) { this.issuer = issuer; }

  public String getHmacSecret() { return hmacSecret; }
  public void setHmacSecret(String hmacSecret) { this.hmacSecret = hmacSecret; }

  public String getKid() { return kid; }
  public void setKid(String kid) { this.kid = kid; }

  public List<String> getAllowedCidrs() { return allowedCidrs; }
  public void setAllowedCidrs(List<String> allowedCidrs) { this.allowedCidrs = allowedCidrs; }
}
